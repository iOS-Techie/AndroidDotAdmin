package com.nyotek.dot.admin.ui.dispatch

import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSServiceConfig
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.repository.BaseRepository
import com.nyotek.dot.admin.repository.NSDispatchRepository
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.responses.AllDispatchListResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListResponse
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

/**
 * Repository class to handle data operations related to Dispatch Detail
 */
object NSDispatchListRepository : BaseRepository() {

    fun getDispatchList(isSwipeRefresh: Boolean, serviceId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        if (NSServiceConfig.getServiceResponse() != null) {
            if (isSwipeRefresh && serviceId.isNotEmpty()) {
                val response = NSServiceConfig.getServiceResponse()
                val list = getList(response)
                getDispatchFromService(serviceId, list, viewModelCallback)
            } else {
                val response = NSServiceConfig.getServiceResponse()
                serviceResponse(response, viewModelCallback)
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                apiManager.getServiceList(object :
                    NSRetrofitCallback<NSGetServiceListResponse>(
                        viewModelCallback,
                        NSApiErrorHandler.ERROR_GET_SERVICE_LIST
                    ) {
                    override fun <T> onResponse(response: Response<T>) {
                        CoroutineScope(Dispatchers.Main).launch {
                            NSServiceConfig.setServiceResponse(response.body() as NSGetServiceListResponse)
                            serviceResponse(NSServiceConfig.getServiceResponse(), viewModelCallback)
                        }
                    }

                    override fun onRefreshToken() {
                        getDispatchList(isSwipeRefresh, serviceId, viewModelCallback)
                    }
                })
            }
        }

    }

    private fun serviceResponse(response: NSGetServiceListResponse?, viewModelCallback: NSGenericViewModelCallback) {
        val list = getList(response)

        if (list.isValidList()) {
            val firstServiceId = list.first().serviceId?:""
            if (firstServiceId.isNotEmpty()) {
                getDispatchFromService(firstServiceId, list, viewModelCallback)
            } else {
                val pair = AllDispatchListResponse(list, arrayListOf())
                viewModelCallback.onSuccess(pair)
            }
        } else {
            val pair = AllDispatchListResponse(list, arrayListOf())
            viewModelCallback.onSuccess(pair)
        }
    }

    private fun getList(response: NSGetServiceListResponse?): MutableList<NSGetServiceListData> {
        response?.data?.sortBy { it.serviceId }
        return response?.data?.filter { it.isActive } as MutableList<NSGetServiceListData>
    }

    private fun getDispatchFromService(
        serviceId: String, serviceList: MutableList<NSGetServiceListData>,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getDispatchesFromService(serviceId, object :
                NSRetrofitCallback<NSDispatchOrderListResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_DISPATCH_FROM_SERVICE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (response.body() is NSDispatchOrderListResponse) {
                            val dispatchData = response.body() as NSDispatchOrderListResponse
                            val sortedData = runBlocking {
                                dispatchData.orderData.map { item ->
                                    async(Dispatchers.Default) {
                                        val firstStatus = item.status.first()
                                        firstStatus.status = NSUtilities.capitalizeFirstLetter(firstStatus.status.replace("_", " "))
                                        item.status[0] = firstStatus
                                        Pair(item, NSDateTimeHelper.getCommonDateView(item.status.first().statusCapturedTime))
                                    }
                                }.awaitAll().sortedByDescending { it.second }.map { it.first }
                            }
                            val list: MutableList<NSDispatchOrderListData> = arrayListOf()
                            list.addAll(sortedData)
                            //dispatchData.orderData.sortByDescending { NSDateTimeHelper.getCommonDateView(it.status.first().statusCapturedTime) }
                            val pair = AllDispatchListResponse(serviceList, list)
                            viewModelCallback.onSuccess(pair)
                        }
                    }
                }

                override fun onRefreshToken() {
                    NSDispatchRepository.getDispatchFromService(serviceId, viewModelCallback)
                }
            })
        }
    }
}
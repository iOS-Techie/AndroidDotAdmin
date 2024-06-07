package com.nyotek.dot.admin.ui.tabs.dispatch

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.AllDispatchListResponse
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListResponse
import com.nyotek.dot.admin.models.responses.NSGetServiceListData
import com.nyotek.dot.admin.models.responses.NSGetServiceListResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class DispatchViewModel @Inject constructor(
    private val repository: Repository,
    languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var selectedPosition: Int = -1
    var isApiCalled = false
    var dispatchListObserve: MutableLiveData<AllDispatchListResponse> = MutableLiveData()
    var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()
    var selectedServiceId: String = ""
    var selectedServiceLogo: String? = null

    fun getDispatchList(isShowProgress: Boolean, serviceId: String = selectedServiceId) = viewModelScope.launch {
        if (isShowProgress) showProgress()
        getBaseServiceList(isShowProgress) {
            val list = getList(it)
            if (serviceId.isNotEmpty()) {
                getDispatchDataList(serviceId, list)
            } else {
                serviceResponse(list)
            }
        }
    }

    private fun serviceResponse(list: MutableList<NSGetServiceListData>) {
        if (list.isValidList()) {
            val firstServiceId = list.first().serviceId?:""
            if (firstServiceId.isNotEmpty()) {
                getDispatchDataList(firstServiceId, list)
            } else {
                val pair = AllDispatchListResponse(list, arrayListOf())
                dispatchListObserve.postValue(pair)
            }
        } else {
            val pair = AllDispatchListResponse(list, arrayListOf())
            dispatchListObserve.postValue(pair)
        }
    }

    private fun getDispatchDataList(serviceId: String = "", serviceList: MutableList<NSGetServiceListData>) = viewModelScope.launch {
        getDispatchDataListApi(serviceId, serviceList)
    }

    private suspend fun getDispatchDataListApi(serviceId: String = "", serviceList: MutableList<NSGetServiceListData>) {
        performApiCalls(
            { repository.remote.dispatchFromService(serviceId) }
        ) { responses, isSuccess ->
            if (isSuccess) {
                val dispatchOrderList = responses[0] as NSDispatchOrderListResponse?
                val sortedData = runBlocking {
                    dispatchOrderList?.orderData?.map { item ->
                        async(Dispatchers.Default) {
                            val firstStatus = item.status.first()
                            Handler(Looper.getMainLooper()).post {
                                firstStatus.status = NSUtilities.capitalizeFirstLetter(
                                    firstStatus.status.replace(
                                        "_",
                                        " "
                                    )
                                )
                                item.status[0] = firstStatus
                            }
                            Pair(
                                item,
                                NSDateTimeHelper.getCommonDateView(item.status.first().statusCapturedTime)
                            )
                        }
                    }?.awaitAll()?.sortedByDescending { it.second }?.map { it.first }
                }
                val list: MutableList<NSDispatchOrderListData> = arrayListOf()
                if (sortedData != null) {
                    list.addAll(sortedData)
                }
                val pair = AllDispatchListResponse(serviceList, list)
                dispatchListObserve.postValue(pair)
            } else {
                val pair = AllDispatchListResponse(serviceList, arrayListOf())
                dispatchListObserve.postValue(pair)
            }
        }
    }

    private fun getList(response: NSGetServiceListResponse?): MutableList<NSGetServiceListData> {
        response?.data?.sortBy { it.serviceId }
        return response?.data?.filter { it.isActive } as MutableList<NSGetServiceListData>
    }

    suspend fun filterDataConcurrently(fleetList: MutableList<NSDispatchOrderListData>, filterTypes: List<String>
    ): MutableList<NSDispatchOrderListData> {
        return coroutineScope {
            // Use async to process each item in fleetList concurrently
            val filteredDataDeferred = fleetList.map { order ->
                async {
                    if (order.status.any { statusFilter ->
                            filterTypes.contains(NSUtilities.capitalizeFirstLetter(statusFilter.status.replace("_", " ")))
                        }) {
                        order
                    } else {
                        null
                    }
                }
            }

            // Wait for all async operations to complete and filter out null results
            val filteredData = filteredDataDeferred.awaitAll().filterNotNull().toMutableList()
            filteredData
        }
    }

    suspend fun filterDataConcurrently(fleetList: MutableList<NSDispatchOrderListData>, searchText: String): MutableList<NSDispatchOrderListData> {
        return coroutineScope {
            // Use async to process each item in fleetList concurrently
            val filteredDataDeferred = fleetList.map { order ->
                async {
                    val userNameMatch = async { order.userMetadata?.userName?.lowercase()?.contains(searchText.lowercase()) ?: false }
                    val userPhoneMatch = async { order.userMetadata?.userPhone?.lowercase()?.contains(searchText.lowercase()) ?: false }

                    // Wait for both async operations to complete and combine the results
                    val userNameMatches = userNameMatch.await()
                    val userPhoneMatches = userPhoneMatch.await()

                    // Return true if either username or user phone matches the search text
                    userNameMatches || userPhoneMatches
                }
            }

            // Wait for all async operations to complete and filter out items that match the search text
            val filteredData = fleetList.filterIndexed { index, _ -> filteredDataDeferred[index].await() }.toMutableList()
            filteredData
        }
    }
}
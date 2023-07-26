package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSCreateServiceRequest
import com.nyotek.dot.admin.repository.network.requests.NSServiceRequest
import com.nyotek.dot.admin.repository.network.responses.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallets
 */
object NSServiceRepository : BaseRepository() {

    /**
     * To get service data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getServiceList(
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getServiceList(object :
                NSRetrofitCallback<NSGetServiceListResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_GET_SERVICE_LIST
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getServiceList(viewModelCallback)
                }
            })
        }
    }

    /**
     * To get wallet balance data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun createService(
        name: String,
        description: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val nsCreateServiceRequest = NSCreateServiceRequest(name, description)
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.createService(nsCreateServiceRequest, object :
                NSRetrofitCallback<NSCreateServiceResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_WALLET_BALANCE_DATA
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    createService(name, description, viewModelCallback)
                }
            })
        }
    }

    /**
     * To get wallet transaction data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun enableDisableService(
        serviceId: String,
        isServiceEnable: Boolean,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val serviceRequest = NSServiceRequest(serviceId)
            val callback = object :
                NSRetrofitCallback<NSBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_WALLET_TRANSACTION_DATA
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    enableDisableService(serviceId, isServiceEnable, viewModelCallback)
                }
            }

            if (isServiceEnable) {
                apiManager.enableService(serviceRequest, callback)
            } else {
                apiManager.disableService(serviceRequest, callback)
            }
        }
    }
}
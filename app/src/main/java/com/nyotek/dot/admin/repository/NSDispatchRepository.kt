package com.nyotek.dot.admin.repository

import com.mapbox.maps.extension.style.expressions.dsl.generated.has
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSUpdateStatusRequest
import com.nyotek.dot.admin.repository.network.responses.DispatchDetailResponse
import com.nyotek.dot.admin.repository.network.responses.FleetLocationResponse
import com.nyotek.dot.admin.repository.network.responses.NSBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListResponse
import com.nyotek.dot.admin.repository.network.responses.NSErrorResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallets
 */
object NSDispatchRepository : BaseRepository() {

    /**
     * To get dispatch list from service API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getDispatchFromService(
        id: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getDispatchesFromService(id, object :
                NSRetrofitCallback<NSDispatchOrderListResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_DISPATCH_FROM_SERVICE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getDispatchFromService(id, viewModelCallback)
                }
            })
        }
    }

    /**
     * To get dispatch detail from API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getDispatchDetail(
        id: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getDispatchDetail(id, object :
                NSRetrofitCallback<DispatchDetailResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_DISPATCH_DETAIL
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getDispatchDetail(id, viewModelCallback)
                }
            })
        }
    }

    /**
     * To update dispatch order status from API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateDispatchOrderStatus(
        id: String, status: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.updateDispatchOrderStatus(id, NSUpdateStatusRequest(status), object :
                NSRetrofitCallback<NSBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_DISPATCH_ORDER_STATUS
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    updateDispatchOrderStatus(id, status, viewModelCallback)
                }
            })
        }
    }
}
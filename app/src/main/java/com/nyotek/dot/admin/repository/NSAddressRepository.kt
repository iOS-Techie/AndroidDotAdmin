package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSAddressRequest
import com.nyotek.dot.admin.repository.network.requests.NSCreateFleetAddressRequest
import com.nyotek.dot.admin.repository.network.requests.NSEditAddressRequest
import com.nyotek.dot.admin.repository.network.responses.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to address
 */
object NSAddressRepository : BaseRepository() {

    /**
     * To get address data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getAddress(
        addressId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val addressRequest = NSAddressRequest(addressId)
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getAddress(addressRequest, object :
                NSRetrofitCallback<GetAddressResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_GET_ADDRESS
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:GetAddressResponse())
                    }
                }

                override fun onRefreshToken() {
                    getAddress(addressId, viewModelCallback)
                }
            })
        }
    }


    /**
     * To createVendorAddress data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun createFleetAddress(
        request: NSCreateFleetAddressRequest,
        viewModelCallback: NSGenericViewModelCallback) {
    CoroutineScope(Dispatchers.IO).launch {
        val callback = object :
            NSRetrofitCallback<NSCreateFleetAddressResponse>(
                viewModelCallback,
                NSApiErrorHandler.REQUEST_CREATE_VENDOR_ADDRESS
            ) {
            override fun <T> onResponse(response: Response<T>) {
                CoroutineScope(Dispatchers.Main).launch {
                    viewModelCallback.onSuccess(response.body())
                }
            }

            override fun onRefreshToken() {
                createFleetAddress(request, viewModelCallback)
            }
        }

        apiManager.createFleetAddress(request, callback)
    }
}

    /**
     * To edit address data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun editAddress(
        addressRequest: NSEditAddressRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.editAddress(addressRequest, object :
                NSRetrofitCallback<NSBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_GET_ADDRESS
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    editAddress(addressRequest, viewModelCallback)
                }
            })
        }
    }
}
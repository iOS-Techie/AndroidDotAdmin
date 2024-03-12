package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.responses.VendorDetailResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to Vendor
 */
object NSVendorRepository : BaseRepository() {

    fun getVendorDetail(vendorId: String,
                        viewModelCallback: NSGenericViewModelCallback
    ) {
        val instance = NSApplication.getInstance()
        if (instance.getVendorDetail(vendorId) != null) {
            viewModelCallback.onSuccess(instance.getVendorDetail(vendorId))
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                apiManager.getVendorDetail(vendorId, object :
                    NSRetrofitCallback<VendorDetailResponse>(
                        viewModelCallback,
                        NSApiErrorHandler.ERROR_VENDOR_DETAIL
                    ) {
                    override fun <T> onResponse(response: Response<T>) {
                        CoroutineScope(Dispatchers.Main).launch {
                            if (response.body() is VendorDetailResponse) {
                                instance.setVendorMap(vendorId, response.body() as VendorDetailResponse)
                            }
                            viewModelCallback.onSuccess(response.body())
                        }
                    }

                    override fun onRefreshToken() {
                        getVendorDetail(vendorId, viewModelCallback)
                    }
                })
            }
        }
    }
}
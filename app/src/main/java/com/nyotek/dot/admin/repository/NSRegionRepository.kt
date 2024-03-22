package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.responses.RegionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to Regions
 */
object NSRegionRepository: BaseRepository() {

    var regions: RegionResponse? = null

    fun getRegions(viewModelCallback: NSGenericViewModelCallback) {
        if (regions != null) {
            viewModelCallback.onSuccess(regions)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                apiManager.getRegions(
                    object : NSRetrofitCallback<RegionResponse>(
                        viewModelCallback, NSApiErrorHandler.ERROR_GET_REGIONS
                    ) {
                        override fun <T> onResponse(response: Response<T>) {
                            CoroutineScope(Dispatchers.Main).launch {
                                regions = response.body() as RegionResponse
                                viewModelCallback.onSuccess(regions)
                            }
                        }

                        override fun onRefreshToken() {
                            getRegions(viewModelCallback)
                        }
                    })
            }
        }
    }
}
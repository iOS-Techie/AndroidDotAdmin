package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.repository.network.requests.NSUpdateCapabilitiesRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleEnableDisableRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleNotesRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleUpdateImageRequest
import com.nyotek.dot.admin.repository.network.responses.NSVehicleBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallets
 */
object NSVehicleRepository : BaseRepository() {

    /**
     * To create vehicle data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun createVehicle(
        request: NSVehicleRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.createVehicle(request, object :
                NSRetrofitCallback<ResponseBody>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_CREATE_VEHICLE_DATA
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    createVehicle(request, viewModelCallback)
                }
            })
        }
    }

    /**
     * To get wallet transaction data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getVehicleList(
        refId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSVehicleResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VEHICLE_LIST
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?: NSVehicleResponse())
                    }
                }

                override fun onRefreshToken() {
                    getVehicleList(refId, viewModelCallback)
                }
            }

            apiManager.vehicleList(refId, callback)
        }
    }

    /**
     * To enable disable vehicle data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun enableDisableVehicle(
        vehicleId: String,
        isServiceEnable: Boolean,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val vendorRequest = NSVehicleEnableDisableRequest(vehicleId)
            val callback = object :
                NSRetrofitCallback<NSVehicleBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VEHICLE_ENABLE_DISABLE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?: NSVehicleBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    enableDisableVehicle(vehicleId, isServiceEnable, viewModelCallback)
                }
            }

            if (isServiceEnable) {
                apiManager.enableVehicle(vendorRequest, callback)
            } else {
                apiManager.disableVehicle(vendorRequest, callback)
            }
        }
    }

    /**
     * To update vehicle image API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateVehicleImage(
        request: NSVehicleUpdateImageRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.updateVehicleImage(request, object :
                NSRetrofitCallback<NSVehicleBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VEHICLE_UPDATE_IMAGE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSVehicleBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateVehicleImage(request, viewModelCallback)
                }
            })
        }
    }

    /**
     * To update vehicle notes API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateVehicleNotes(
        request: NSVehicleNotesRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.updateVehicleNotes(request, object :
                NSRetrofitCallback<NSVehicleBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VEHICLE_UPDATE_NOTES
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSVehicleBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateVehicleNotes(request, viewModelCallback)
                }
            })
        }
    }

    /**
     * To update vehicle notes API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateVehicleCapability(
        request: NSUpdateCapabilitiesRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.updateVehicleCapability(request, object :
                NSRetrofitCallback<NSVehicleBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VEHICLE_UPDATE_CAPABILITY
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSVehicleBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateVehicleCapability(request, viewModelCallback)
                }
            })
        }
    }

    /**
     * To get vehicle details API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getVehicleDetail(
        id: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getVehicleDetail(id, object :
                NSRetrofitCallback<NSVehicleDetailResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VEHICLE_DETAIL
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSVehicleDetailResponse())
                    }
                }

                override fun onRefreshToken() {
                    getVehicleDetail(id, viewModelCallback)
                }
            })
        }
    }

    /**
     * To assign vehicle API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun assignVehicle(
        request: NSAssignVehicleRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.assignVehicle(request, object :
                NSRetrofitCallback<NSVehicleBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_ASSIGN_VEHICLE_DETAIL
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSVehicleBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    assignVehicle(request, viewModelCallback)
                }
            })
        }
    }
}
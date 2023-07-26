package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSCapabilitiesRequest
import com.nyotek.dot.admin.repository.network.requests.NSCreateCapabilityRequest
import com.nyotek.dot.admin.repository.network.requests.NSServiceCapabilitiesRequest
import com.nyotek.dot.admin.repository.network.requests.NSServiceFleetUpdateRequest
import com.nyotek.dot.admin.repository.network.responses.NSCapabilitiesBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSCapabilitiesResponse
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSServiceCapabilityResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Repository class to handle data operations related to capabilities
 */
object NSCapabilitiesRepository: BaseRepository() {

    /**
     * To get capabilities list
     *
     * @param viewModelCallback The callback to communicate back to view model
     */
    fun getCapabilities(viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getCapabilities(object : NSRetrofitCallback<NSCapabilitiesResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_CAPABILITIES_LIST
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getCapabilities(viewModelCallback)
                }
            })
        }
    }

    /**
     * To get service capability id
     *
     * @param viewModelCallback The callback to communicate back to view model
     */
    fun getServiceCapabilities(serviceId: String, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getServiceCapability(serviceId, object : NSRetrofitCallback<NSServiceCapabilityResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_SERVICE_CAPABILITIES_LIST
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getServiceCapabilities(serviceId, viewModelCallback)
                }
            })
        }
    }

    /**
     * To enable disable Capabilities data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun enableDisableCapabilities(
        capabilityId: String,
        isServiceEnable: Boolean,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = NSCapabilitiesRequest(capabilityId)
            val callback = object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_CAPABILITIES_ENABLE_DISABLE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?: NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    enableDisableCapabilities(capabilityId, isServiceEnable, viewModelCallback)
                }
            }

            if (isServiceEnable) {
                apiManager.enableCapabilities(request, callback)
            } else {
                apiManager.disableCapabilities(request, callback)
            }
        }
    }

    /**
     * To delete capabilities data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun deleteCapability(
        id: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.capabilityDelete(id, object :
                NSRetrofitCallback<NSCapabilitiesBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_CAPABILITY_DELETE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?: NSCapabilitiesBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    deleteCapability(id, viewModelCallback)
                }
            })
        }
    }

    /**
     * To create capability data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun createEditCapability(isCreate: Boolean, id: String = "",
        request: NSCreateCapabilityRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<ResponseBody>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_CREATE_EDIT_CAPABILITY
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    createEditCapability(isCreate, id, request, viewModelCallback)
                }
            }

            if (isCreate) {
                apiManager.createCapability(request, callback)
            } else {
                apiManager.updateCapability(id, request, callback)
            }
        }
    }

    /**
     * To delete capabilities data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateServiceCapability(
        serviceId: String,
        capabilityId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = NSServiceCapabilitiesRequest(serviceId, capabilityId)
            apiManager.updateServiceCapability(request, object :
                NSRetrofitCallback<NSCapabilitiesBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_CAPABILITY_SERVICE_UPDATE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?: NSCapabilitiesBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateServiceCapability(serviceId, capabilityId, viewModelCallback)
                }
            })
        }
    }

    /**
     * To update fleets data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateServiceFleets(
        serviceId: String,
        fleets: List<String>,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = NSServiceFleetUpdateRequest(serviceId, fleets)
            apiManager.updateServiceFleets(request, object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_SERVICE_FLEET_UPDATE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?: NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateServiceFleets(serviceId, fleets, viewModelCallback)
                }
            })
        }
    }
}

package com.nyotek.dot.admin.repository

import com.google.gson.Gson
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.*

import com.nyotek.dot.admin.repository.network.responses.*
import com.nyotek.dot.admin.repository.network.responses.FleetListResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallets
 */
object NSFleetRepository: BaseRepository() {
    val gson = Gson()

    /**
     * To get wallet data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getFleetList(viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getFleetList(object :
                NSRetrofitCallback<FleetListResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_APP_THEME
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getFleetList(viewModelCallback)
                }
            })
        }
    }

    /**
     * To get wallet transaction data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun enableDisableFleet(
        vendorId: String,
        isServiceEnable: Boolean,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val vendorRequest = NSFleetRequest(vendorId)
            val callback = object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VENDOR_ENABLE_DISABLE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    enableDisableFleet(vendorId, isServiceEnable, viewModelCallback)
                }
            }

            if (isServiceEnable) {
                apiManager.enableFleet(vendorRequest, callback)
            } else {
                apiManager.disableFleet(vendorRequest, callback)
            }
        }
    }

    /**
     * To get wallet transaction data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateFleetLogo(
        updateRequest: NSFleetLogoUpdateRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSUpdateFleetLogoResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VEHICLE_LIST
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSUpdateFleetLogoResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateFleetLogo(updateRequest, viewModelCallback)
                }
            }

            apiManager.updateFleetLogo(updateRequest, callback)
        }
    }

    /**
     * To get wallet transaction data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateFleetLogoScale(
        updateRequest: NSFleetLogoScaleRequest,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_VEHICLE_LIST
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateFleetLogoScale(updateRequest, viewModelCallback)
                }
            }

            apiManager.updateFleetScaleLogo(updateRequest, callback)
        }
    }

    /**
     * To get createVendor data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun createFleet(createCompanyRequest: NSCreateCompanyRequest, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSCreateCompanyResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.REQUEST_CREATE_VENDOR
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body() ?: NSCreateCompanyResponse())
                    }
                }

                override fun onRefreshToken() {
                    createFleet(createCompanyRequest, viewModelCallback)
                }
            }
            apiManager.createFleet(createCompanyRequest, callback)
        }
    }

    /**
     * To update vendor name data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateFleetName(
        vendorId: String,
        nameList: HashMap<String, String>,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val updateRequest = NSFleetNameUpdateRequest(vendorId, nameList)
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_UPDATE_NAME
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateFleetName(vendorId, nameList, viewModelCallback)
                }
            }

            apiManager.updateFleetName(updateRequest, callback)
        }
    }

    /**
     * To update vendor name data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateFleetrSlogan(
        vendorId: String,
        sloganList: HashMap<String, String>,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val updateRequest = NSFleetSloganUpdateRequest(vendorId, sloganList)
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_UPDATE_SLOGAN
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateFleetrSlogan(vendorId, sloganList, viewModelCallback)
                }
            }

            apiManager.updateFleetSlogan(updateRequest, callback)
        }
    }

    /**
     * To update vendor name data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateFleetUrl(
        vendorId: String,
        url: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val updateRequest = NSFleetUrlUpdateRequest(vendorId, url)
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_UPDATE_URL
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateFleetUrl(vendorId, url, viewModelCallback)
                }
            }

            apiManager.updateFleetUrl(updateRequest, callback)
        }
    }

    /**
     * To update vendor name data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateFleetServiceIds(
        vendorId: String,
        serviceIds: List<String>,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val updateRequest = NSFleetServiceIdsUpdateRequest(vendorId, serviceIds)
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_UPDATE_SERVICE_IDS
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateFleetServiceIds(vendorId, serviceIds, viewModelCallback)
                }
            }

            apiManager.updateFleetServiceIds(updateRequest, callback)
        }
    }

    /**
     * To update vendor name data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateFleetTags(
        vendorId: String,
        tags: List<String>,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val updateRequest = NSFleetUpdateTagsRequest(vendorId, tags)
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<NSFleetBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_UPDATE_VENDOR_TAGS
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSFleetBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateFleetTags(vendorId, tags, viewModelCallback)
                }
            }

            apiManager.updateFleetTags(updateRequest, callback)
        }
    }

    /**
     * To update vendor name data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getFleetDetail(
        vendorId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val vendorRequest = NSFleetRequest(vendorId)
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<FleetSingleResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.REQUEST_VENDOR_DETAIL
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:FleetSingleResponse())
                    }
                }

                override fun onRefreshToken() {
                    getFleetDetail(vendorId, viewModelCallback)
                }
            }

            apiManager.getFleetDetail(vendorRequest, callback)
        }
    }

    /**
     * To get fleet locations
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getFleetLocations(
        fleetId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<FleetLocationResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_FLEET_LOCATION
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:FleetLocationResponse())
                    }
                }

                override fun onRefreshToken() {
                    getFleetLocations(fleetId, viewModelCallback)
                }
            }

            apiManager.getFleetLocation(fleetId, callback)
        }
    }

    /**
     * To get fleet locations
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getDriverLocations(
        driverId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<FleetLocationResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_DRIVER_LOCATION
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:FleetLocationResponse())
                    }
                }

                override fun onRefreshToken() {
                    getDriverLocations(driverId, viewModelCallback)
                }
            }

            apiManager.getDriverLocation(driverId, callback)
        }
    }

    /**
     * To get fleet driver locations
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getFleetDriverLocations(
        driverIds: List<String>,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val callback = object :
                NSRetrofitCallback<FleetLocationResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_FLEET_DRIVER_LOCATION
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:FleetLocationResponse())
                    }
                }

                override fun onRefreshToken() {
                    getFleetDriverLocations(driverIds, viewModelCallback)
                }
            }

            apiManager.getFleetDriverLocation(NSFleetDriverRequest(driverIds), callback)
        }
    }
}
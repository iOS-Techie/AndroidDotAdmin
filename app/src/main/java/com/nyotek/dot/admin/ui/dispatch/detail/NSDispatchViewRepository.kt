package com.nyotek.dot.admin.ui.dispatch.detail

import com.nyotek.dot.admin.common.utils.Quadruple
import com.nyotek.dot.admin.repository.BaseRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.manager.NSApiManager
import com.nyotek.dot.admin.repository.network.responses.DispatchDetailResponse
import com.nyotek.dot.admin.repository.network.responses.FleetLocationResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchDetailAllResponse
import com.nyotek.dot.admin.repository.network.responses.NSDocumentListResponse
import com.nyotek.dot.admin.repository.network.responses.NSDriverVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.VendorDetailResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Repository class to handle data operations related to Dispatch Detail
 */
object NSDispatchViewRepository : BaseRepository() {

    fun getDispatchLocationHistory(
        dispatchId: String, vendorId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getLocationHistoryDispatch(dispatchId, object :
                NSRetrofitCallback<FleetLocationResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_DISPATCH_LOCATION_HISTORY
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    if (response.body() is FleetLocationResponse) {
                        val fleetLocation = response.body() as FleetLocationResponse
                        val features = fleetLocation.fleetDataItem?.features?.sortedByDescending { it.properties?.createdAt }?: arrayListOf()
                        if (features.isNotEmpty()) {
                            val location = features.first().properties
                            if (location != null) {
                                getAllDetailsAndProcess(dispatchId, vendorId, location.vehicleId?:"", location.driverId?:"", fleetLocation, viewModelCallback)
                            } else {
                                getAllDetailsAndProcess(dispatchId, vendorId, "", "", fleetLocation, viewModelCallback)
                            }
                        } else {
                            getAllDetailsAndProcess(dispatchId, vendorId, "", "", fleetLocation, viewModelCallback)
                        }
                    }
                }

                override fun onRefreshToken() {
                    getDispatchLocationHistory(dispatchId, vendorId, viewModelCallback)
                }
            })
        }
    }

    fun getAllDetailsAndProcess(dispatchId: String, vendorId: String, vehicleId: String, driverId: String, location: FleetLocationResponse, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.Main).launch {
            val (dispatchResponse, driverResponse, vendorResponse, vehicleResponse) = getAllDetails(dispatchId, driverId, vendorId, vehicleId, viewModelCallback)

            // Process responses
            viewModelCallback.onSuccess(NSDispatchDetailAllResponse(location, driverResponse, dispatchResponse, vendorResponse, vehicleResponse))
        }
    }

    private suspend fun getAllDetails(
        dispatchId: String,
        driverId: String,
        vendorId: String,
        vehicleId: String, viewModelCallback: NSGenericViewModelCallback
    ): Quadruple<DispatchDetailResponse?, NSDocumentListResponse?, VendorDetailResponse?, NSDriverVehicleDetailResponse?> {
        val dispatchResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDispatchDetailAsync(dispatchId, viewModelCallback)
        }
        val driverResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDriverDocumentDetailAsync(driverId, viewModelCallback)
        }
        val vendorResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getVendorDetailAsync(vendorId, viewModelCallback)
        }
        val vehicleResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDriverVehicleDetailAsync(vehicleId, viewModelCallback)
        }

        val dispatchResponse = dispatchResponseDeferred.await()
        val driverResponse = driverResponseDeferred.await()
        val vendorResponse = vendorResponseDeferred.await()
        val vehicleResponse = vehicleResponseDeferred.await()

        return Quadruple(dispatchResponse, driverResponse, vendorResponse, vehicleResponse)
    }

    suspend fun NSApiManager.getDispatchDetailAsync(
        dispatchId: String,
        viewModelCallback: NSGenericViewModelCallback
    ): DispatchDetailResponse? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (dispatchId.isEmpty()) {
                    continuation.resume(DispatchDetailResponse())
                } else {
                    getDispatchDetail(
                        dispatchId,
                        object : NSRetrofitCallback<DispatchDetailResponse>(
                            viewModelCallback,
                            NSApiErrorHandler.ERROR_DISPATCH_DETAIL
                        ) {

                            override fun <T> onResponse(response: Response<T>) {
                                if (response.body() is DispatchDetailResponse) {
                                    continuation.resume(response.body() as DispatchDetailResponse)
                                }
                            }

                            override fun <T> onErrorResponse(response: Response<T>) {
                                super.onErrorResponse(response)
                                continuation.resume(DispatchDetailResponse())
                            }

                            override fun onRefreshToken() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getDispatchDetailAsync(dispatchId, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }

    suspend fun NSApiManager.getVendorDetailAsync(
        vendorId: String,
        viewModelCallback: NSGenericViewModelCallback
    ): VendorDetailResponse? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (vendorId.isEmpty()) {
                    continuation.resume(VendorDetailResponse())
                } else {
                    getVendorDetail(
                        vendorId,
                        object : NSRetrofitCallback<VendorDetailResponse>(
                            viewModelCallback,
                            NSApiErrorHandler.ERROR_VENDOR_DETAIL
                        ) {

                            override fun <T> onResponse(response: Response<T>) {
                                if (response.body() is VendorDetailResponse) {
                                    continuation.resume(response.body() as VendorDetailResponse)
                                }
                            }

                            override fun <T> onErrorResponse(response: Response<T>) {
                                super.onErrorResponse(response)
                                continuation.resume(VendorDetailResponse())
                            }

                            override fun onRefreshToken() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getVendorDetailAsync(vendorId, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }

    suspend fun NSApiManager.getDriverVehicleDetailAsync(
        vehicleId: String,
        viewModelCallback: NSGenericViewModelCallback
    ): NSDriverVehicleDetailResponse? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (vehicleId.isEmpty()) {
                    continuation.resume(NSDriverVehicleDetailResponse())
                } else {
                    getDriverVehicleDetail(
                        vehicleId,
                        object : NSRetrofitCallback<NSDriverVehicleDetailResponse>(
                            viewModelCallback,
                            NSApiErrorHandler.ERROR_DRIVER_VEHICLE_DETAIL
                        ) {

                            override fun <T> onResponse(response: Response<T>) {
                                if (response.body() is NSDriverVehicleDetailResponse) {
                                    continuation.resume(response.body() as NSDriverVehicleDetailResponse)
                                }
                            }

                            override fun <T> onErrorResponse(response: Response<T>) {
                                super.onErrorResponse(response)
                                continuation.resume(NSDriverVehicleDetailResponse())
                            }

                            override fun onRefreshToken() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getDriverVehicleDetailAsync(vehicleId, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }

    suspend fun NSApiManager.getDriverDocumentDetailAsync(
        driverId: String,
        viewModelCallback: NSGenericViewModelCallback
    ): NSDocumentListResponse? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (driverId.isEmpty()) {
                    continuation.resume(NSDocumentListResponse())
                } else {
                    getDocumentList(
                        driverId,
                        object : NSRetrofitCallback<NSDocumentListResponse>(
                            viewModelCallback,
                            NSApiErrorHandler.ERROR_DOCUMENT_LIST
                        ) {

                            override fun <T> onResponse(response: Response<T>) {
                                if (response.body() is NSDocumentListResponse) {
                                    continuation.resume(response.body() as NSDocumentListResponse)
                                }
                            }

                            override fun <T> onErrorResponse(response: Response<T>) {
                                super.onErrorResponse(response)
                                continuation.resume(NSDocumentListResponse())
                            }

                            override fun onRefreshToken() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getDriverDocumentDetailAsync(driverId, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }
}
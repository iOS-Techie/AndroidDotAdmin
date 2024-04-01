package com.nyotek.dot.admin.ui.dispatch.detail

import com.nyotek.dot.admin.common.utils.QuadrupleSix
import com.nyotek.dot.admin.repository.BaseRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.manager.NSApiManager
import com.nyotek.dot.admin.repository.network.requests.NSUpdateStatusRequest
import com.nyotek.dot.admin.repository.network.responses.DispatchDetailResponse
import com.nyotek.dot.admin.repository.network.responses.DispatchRequestListResponse
import com.nyotek.dot.admin.repository.network.responses.DriverListModel
import com.nyotek.dot.admin.repository.network.responses.FleetLocationResponse
import com.nyotek.dot.admin.repository.network.responses.NSBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchDetailAllResponse
import com.nyotek.dot.admin.repository.network.responses.NSDocumentListResponse
import com.nyotek.dot.admin.repository.network.responses.NSDriverVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.NSErrorResponse
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
        dispatchId: String, vendorId: String, driverId: String, serviceId: String?, isThirdParty: Boolean,
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
                                getAllDetailsAndProcess(dispatchId, vendorId, location.vehicleId?:"",
                                    driverId.ifEmpty { location.driverId?:"" }, serviceId, fleetLocation, isThirdParty, viewModelCallback)
                            } else {
                                getAllDetailsAndProcess(dispatchId, vendorId, "", driverId, serviceId, fleetLocation, isThirdParty, viewModelCallback)
                            }
                        } else {
                            getAllDetailsAndProcess(dispatchId, vendorId, "", driverId, serviceId, fleetLocation, isThirdParty, viewModelCallback)
                        }
                    }
                }

                override fun onRefreshToken() {
                    getDispatchLocationHistory(dispatchId, vendorId, driverId, serviceId, isThirdParty, viewModelCallback)
                }
            })
        }
    }

    private fun getAllDetailsAndProcess(dispatchId: String, vendorId: String, vehicleId: String, driverId: String, serviceId: String?, location: FleetLocationResponse, isThirdParty: Boolean, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.Main).launch {
            val (dispatchResponse, driverResponse, vendorResponse, vehicleResponse, dispatchRequests, driverList) = getAllDetails(dispatchId, driverId, vendorId, serviceId, vehicleId, isThirdParty, viewModelCallback)

            // Process responses
            viewModelCallback.onSuccess(NSDispatchDetailAllResponse(location, driverResponse, dispatchResponse, vendorResponse, vehicleResponse, dispatchRequests, driverList, driverId))
        }
    }

    private suspend fun getAllDetails(
        dispatchId: String,
        driverId: String,
        vendorId: String,
        serviceId: String?,
        vehicleId: String, isThirdParty: Boolean, viewModelCallback: NSGenericViewModelCallback
    ): QuadrupleSix<DispatchDetailResponse?, NSDocumentListResponse?, VendorDetailResponse?, NSDriverVehicleDetailResponse?, DispatchRequestListResponse?, DriverListModel?> {
        val dispatchResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDispatchDetailAsync(dispatchId, viewModelCallback)
        }
        val driverResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDriverDocumentDetailAsync(driverId, viewModelCallback)
        }
        val vendorResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getVendorDetailAsync(vendorId, isThirdParty, viewModelCallback)
        }
        val vehicleResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDriverVehicleDetailAsync(vehicleId, viewModelCallback)
        }

        val dispatchRequestResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDispatchRequestDetailAsync(dispatchId, viewModelCallback)
        }

        val driverListResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDriverListAsync(serviceId, viewModelCallback)
        }

        val dispatchResponse = dispatchResponseDeferred.await()
        val driverResponse = driverResponseDeferred.await()
        val vendorResponse = vendorResponseDeferred.await()
        val vehicleResponse = vehicleResponseDeferred.await()
        val dispatchRequestResponse = dispatchRequestResponseDeferred.await()
        val driverListResponse = driverListResponseDeferred.await()

        return QuadrupleSix(dispatchResponse, driverResponse, vendorResponse, vehicleResponse, dispatchRequestResponse, driverListResponse)
    }

    private suspend fun NSApiManager.getDispatchDetailAsync(
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

    private suspend fun NSApiManager.getDispatchRequestDetailAsync(
        dispatchId: String,
        viewModelCallback: NSGenericViewModelCallback
    ): DispatchRequestListResponse? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (dispatchId.isEmpty()) {
                    continuation.resume(DispatchRequestListResponse())
                } else {
                    getDispatchRequestDetail(
                        dispatchId,
                        object : NSRetrofitCallback<DispatchRequestListResponse>(
                            viewModelCallback,
                            NSApiErrorHandler.ERROR_DISPATCH_REQUEST_DETAIL
                        ) {

                            override fun <T> onResponse(response: Response<T>) {
                                if (response.body() is DispatchRequestListResponse) {
                                    continuation.resume(response.body() as DispatchRequestListResponse)
                                }
                            }

                            override fun <T> onErrorResponse(response: Response<T>) {
                                super.onErrorResponse(response)
                                continuation.resume(DispatchRequestListResponse())
                            }

                            override fun onRefreshToken() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getDispatchRequestDetailAsync(dispatchId, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }

    //When Dispatch List From Get isThirdParty false then Api call
    private suspend fun NSApiManager.getVendorDetailAsync(
        vendorId: String, isThirdParty: Boolean,
        viewModelCallback: NSGenericViewModelCallback
    ): VendorDetailResponse? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (vendorId.isEmpty() || isThirdParty) {
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
                                    getVendorDetailAsync(vendorId, isThirdParty, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }

    private suspend fun NSApiManager.getDriverVehicleDetailAsync(
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

    private suspend fun NSApiManager.getDriverListAsync(
        serviceId: String?,
        viewModelCallback: NSGenericViewModelCallback
    ): DriverListModel? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (serviceId.isNullOrEmpty()) {
                    continuation.resume(DriverListModel())
                } else {
                    getDriverList(
                        serviceId,
                        object : NSRetrofitCallback<DriverListModel>(
                            viewModelCallback,
                            NSApiErrorHandler.ERROR_DRIVER_LIST_DISPATCH
                        ) {

                            override fun <T> onResponse(response: Response<T>) {
                                if (response.body() is DriverListModel) {
                                    continuation.resume(response.body() as DriverListModel)
                                }
                            }

                            override fun <T> onErrorResponse(response: Response<T>) {
                                super.onErrorResponse(response)
                                continuation.resume(DriverListModel())
                            }

                            override fun onRefreshToken() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getDriverListAsync(serviceId, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }

    suspend fun getDriverDocument(driverId: String, viewModelCallback: NSGenericViewModelCallback) {
        val driverResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getDriverDocumentDetailAsync(driverId, viewModelCallback)
        }
        val driverDocumentResponse = driverResponseDeferred.await()
        viewModelCallback.onSuccess(driverDocumentResponse)
    }

    private suspend fun NSApiManager.getDriverDocumentDetailAsync(
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


    /*------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //Order Cancel
    /*------------------------------------------------------------------------------------------------------------------------------------------------------*/


    fun updateDispatchOrderStatus(
        dispatchId: String, status: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.updateDispatchOrderStatus(dispatchId, NSUpdateStatusRequest(status), object :
                NSRetrofitCallback<NSBlankDataResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_DISPATCH_ORDER_STATUS
                ) {

                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    updateDispatchOrderStatus(dispatchId, status, viewModelCallback)
                }
            })
        }
    }

    fun assignDriver(
        dispatchId: String, driverId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val map: HashMap<String, String> = hashMapOf()
            map["driver_id"] = driverId
            apiManager.assignDriver(dispatchId, map, object :
                NSRetrofitCallback<NSErrorResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_ASSIGN_DRIVER
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSBlankDataResponse())
                    }
                }

                override fun onRefreshToken() {
                    assignDriver(dispatchId, driverId, viewModelCallback)
                }
            })
        }
    }
}
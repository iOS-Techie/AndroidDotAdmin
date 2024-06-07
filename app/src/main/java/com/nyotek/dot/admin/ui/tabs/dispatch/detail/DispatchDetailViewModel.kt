package com.nyotek.dot.admin.ui.tabs.dispatch.detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSUpdateStatusRequest
import com.nyotek.dot.admin.models.responses.DispatchDetailResponse
import com.nyotek.dot.admin.models.responses.DispatchRequestListResponse
import com.nyotek.dot.admin.models.responses.DocumentDataItem
import com.nyotek.dot.admin.models.responses.DriverListModel
import com.nyotek.dot.admin.models.responses.FleetDataItem
import com.nyotek.dot.admin.models.responses.FleetLocationResponse
import com.nyotek.dot.admin.models.responses.NSDispatchDetailAllResponse
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.models.responses.NSDocumentListResponse
import com.nyotek.dot.admin.models.responses.NSDriverVehicleDetailResponse
import com.nyotek.dot.admin.models.responses.VendorDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DispatchDetailViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var selectedServiceId: String? = null
    var dispatchSelectedData: NSDispatchOrderListData? = null
    var isMapReset: Boolean = false
    var currentMapFleetData: FleetDataItem? = null
    var dispatchListObserve: MutableLiveData<NSDispatchDetailAllResponse> = MutableLiveData()
    var updateOrderObserve: MutableLiveData<Boolean> = MutableLiveData()
    var assignDriverObserve: MutableLiveData<String> = MutableLiveData()

    fun getDispatchFromList(strDispatch: String?) {
        if (strDispatch?.isNotEmpty() == true) {
            dispatchSelectedData = Gson().fromJson(strDispatch, NSDispatchOrderListData::class.java)
        }
    }

    fun getDispatchDetail() = viewModelScope.launch {
        getDispatchDetailApi()
    }

    private suspend fun getDispatchDetailApi() {
        if (dispatchSelectedData != null) {
            if (dispatchSelectedData?.dispatchId?.isNotEmpty() == true) {
                val dispatchId = dispatchSelectedData?.dispatchId ?: ""
                val vendorId = dispatchSelectedData?.vendorId ?: ""
                val driverId = dispatchSelectedData?.assignedDriverId ?: ""
                val isThirdParty = dispatchSelectedData?.isThirdParty ?: false
                val serviceId = selectedServiceId
                showProgress()
                performApiCalls(
                    { repository.remote.getLocationHistoryDispatch(dispatchSelectedData?.dispatchId!!) }
                ) { responses, isSuccess ->
                    if (isSuccess) {
                        val fleetLocation = responses[0] as FleetLocationResponse?
                        val features =
                            fleetLocation?.fleetDataItem?.features?.sortedByDescending { it.properties?.createdAt }
                                ?: arrayListOf()
                        if (features.isNotEmpty()) {
                            val location = features.first().properties
                            if (location != null) {
                                getAllDetailsAndProcess(
                                    dispatchId,
                                    vendorId,
                                    location.vehicleId ?: "",
                                    driverId.ifEmpty { location.driverId ?: "" },
                                    serviceId,
                                    fleetLocation,
                                    isThirdParty
                                )
                            } else {
                                getAllDetailsAndProcess(
                                    dispatchId,
                                    vendorId,
                                    "",
                                    driverId,
                                    serviceId,
                                    fleetLocation,
                                    isThirdParty
                                )
                            }
                        } else {
                            getAllDetailsAndProcess(
                                dispatchId,
                                vendorId,
                                "",
                                driverId,
                                serviceId,
                                fleetLocation,
                                isThirdParty
                            )
                        }
                    } else {
                        hideProgress()
                    }
                }
            }
        }
    }

    private fun getAllDetailsAndProcess(
        dispatchId: String,
        vendorId: String,
        vehicleId: String,
        driverId: String,
        serviceId: String?,
        location: FleetLocationResponse?,
        isThirdParty: Boolean
    ) = viewModelScope.launch {
        getAllDetailsAndProcessApi(
            dispatchId,
            vendorId,
            vehicleId,
            driverId,
            serviceId,
            location,
            isThirdParty
        )
    }

    private suspend fun getAllDetailsAndProcessApi(
        dispatchId: String,
        vendorId: String,
        vehicleId: String,
        driverId: String,
        serviceId: String?,
        location: FleetLocationResponse?,
        isThirdParty: Boolean
    ) {
        performApiCalls(
            {
                if (driverId.isNotEmpty()) {
                    repository.remote.getDriverDocumentInfo(driverId)
                } else {
                    null
                }
            }, {
                if (vendorId.isNotEmpty() && !isThirdParty) {
                    repository.remote.getVendorInfo(vendorId)
                } else {
                    null
                }
            }, {
                if (vehicleId.isNotEmpty()) {
                    repository.remote.getDriverVehicleDetail(vehicleId)
                } else {
                    null
                }
            }, {
                if (serviceId?.isNotEmpty() == true) {
                    repository.remote.getDriverList(serviceId)
                } else {
                    null
                }
            }, {
                if (dispatchId.isNotEmpty()) {
                    repository.remote.dispatchDetail(dispatchId)
                } else {
                    null
                }
            }, {
                if (dispatchId.isNotEmpty()) {
                    repository.remote.getDispatchRequestDetail(dispatchId)
                } else {
                    null
                }
            }) { responses, isSuccess ->

            if (isSuccess) {
                val model = NSDispatchDetailAllResponse(location = location, driverId = driverId)
                for (data in responses) {
                    when (data) {

                        is NSDocumentListResponse? -> {
                            model.driverDetail = data
                        }

                        is DispatchDetailResponse? -> {
                            model.dispatchDetail = data
                        }

                        is VendorDetailResponse? -> {
                            model.vendorDetail = data
                        }

                        is NSDriverVehicleDetailResponse? -> {
                            model.driverVehicleDetail = data
                        }

                        is DispatchRequestListResponse? -> {
                            model.dispatchRequest = data
                        }

                        is DriverListModel? -> {
                            model.driverListModel = data
                        }
                    }
                }

                dispatchListObserve.postValue(model)
            }
            hideProgress()
        }
    }

    fun getDriverDetail(list: MutableList<DocumentDataItem>?): DocumentDataItem? {
        return list?.find { it.documentType == "Driving licence" }
    }

    fun updateOrderStatus(dispatchId: String, status: String) = viewModelScope.launch {
        updateOrderStatusApi(dispatchId, NSUpdateStatusRequest(status))
    }

    private suspend fun updateOrderStatusApi(dispatchId: String, status: NSUpdateStatusRequest) {
        showProgress()
        performApiCalls(
            { repository.remote.updateDispatchOrderStatus(dispatchId, status) }
        ) { _, isSuccess ->
            hideProgress()
            if (isSuccess) {
                updateOrderObserve.postValue(true)
            }
        }
    }

    fun assignDriver(dispatchId: String, driverId: String) = viewModelScope.launch {
        assignDriverApi(dispatchId, driverId)
    }

    private suspend fun assignDriverApi(orderId: String, driverId: String) {
        showProgress()
        val map: HashMap<String, String> = hashMapOf()
        map["driver_id"] = driverId
        performApiCalls(
            { repository.remote.assignDriver(orderId, map) }
        ) { _, isSuccess ->
            hideProgress()
            if (isSuccess) {
                assignDriverObserve.postValue(driverId)
            }
        }
    }

    fun getServiceLogo(serviceId: String?, callback: (String?) -> Unit) {
        if (serviceId?.isNotEmpty() == true) {
            getBaseServiceList(false) { data ->
                callback.invoke(data?.data?.find { it.serviceId == serviceId }?.logoUrl)
            }
        }
    }
}
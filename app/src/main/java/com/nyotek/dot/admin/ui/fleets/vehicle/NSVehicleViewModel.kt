package com.nyotek.dot.admin.ui.fleets.vehicle

import android.app.Application
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.repository.NSVehicleRepository
import com.nyotek.dot.admin.repository.network.requests.NSVehicleRequest
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSVehicleResponse
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem

class NSVehicleViewModel(application: Application) : NSViewModel(application) {
    var ownerId: String? = null
    var strVehicleDetail: String? = null
    var fleetModel: FleetData? = null
    var uploadFileUrl: String? = null

    fun getVehicleDetail(isShowProgress: Boolean, callback: ((MutableList<VehicleDataItem>) -> Unit)) {
        if (!strVehicleDetail.isNullOrEmpty()) {
            if (fleetModel == null) {
                fleetModel = Gson().fromJson(strVehicleDetail, FleetData::class.java)
            }
            ownerId = fleetModel?.vendorId
            if (ownerId?.isNotEmpty() == true) {
                getCapabilities(isShowProgress, ownerId!!, isShowError = true, false, callback) {}
            } else {
                callback.invoke(arrayListOf())
            }
        }
    }

    fun getCapabilities(
        isShowProgress: Boolean,
        id: String? = null,
        isShowError: Boolean = true,
        isCapabilityCheck: Boolean = false,
        callback: ((MutableList<VehicleDataItem>) -> Unit)? = null,
        capCallback: ((MutableList<CapabilitiesDataItem>) -> Unit?)
    ) {
        if (isShowProgress) showProgress()
        getCapabilitiesList(isShowError = isShowError, isCapabilityCheck = isCapabilityCheck) {
            if (id != null && callback != null) {
                getVehicleList(false, id, it, false, callback)
            } else {
                hideProgress()
                capCallback.invoke(it)
            }
        }
    }

    /**
     * Get vehicle list
     *
     */
    fun getVehicleList(isShowProgress: Boolean, refId: String, list: MutableList<CapabilitiesDataItem>, isFromDriverDetail: Boolean, callback: (MutableList<VehicleDataItem>) -> Unit) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            NSVehicleRepository.getVehicleList(refId, obj)
        }, { data, _ ->
            if (!isFromDriverDetail) {
                hideProgress()
            }
            if (data is NSVehicleResponse) {
                data.data.sortBy { it.id }

                for (vehicle in data.data) {
                    vehicle.capabilityNameList = list.filter { vehicle.capabilities.contains(it.id) }
                        .joinToString { getLngValue(it.label) }
                }

                callback.invoke(data.data)
            }
        })
    }

    /**
     * Vehicle Enable Disable
     *
     */
    fun vehicleEnableDisable(vehicleId: String?, isEnable: Boolean) {
        if (vehicleId != null) {
            callCommonApi({ obj ->
                NSVehicleRepository.enableDisableVehicle(vehicleId, isEnable, obj)
            }, { _, _ ->

            })
        }
    }

    fun createVehicle(capabilities: MutableList<String>, detail: HashMap<String, String>, callback: ((Boolean) -> Unit)){
        isProgressShowing.value = true
        val request = NSVehicleRequest()
        request.refId = fleetModel?.vendorId
        request.refType = "fleet"
        request.capabilities = capabilities
        request.model = detail[NSConstants.MODEL]
        request.manufacturer = detail[NSConstants.MANUFACTURE]
        request.manufacturingYear = detail[NSConstants.MANUFACTURE_YEAR]
        request.loadCapacity = detail[NSConstants.LOAD_CAPACITY]
        request.additionalNote = detail[NSConstants.NOTES]
        request.registrationNo = detail[NSConstants.REGISTRATION_NO]
        request.vehicleImg = uploadFileUrl

        callCommonApi({ obj ->
            NSVehicleRepository.createVehicle(request, obj)
        }, { _, isSuccess ->
            callback.invoke(isSuccess)
            hideProgress()
            if (isSuccess) {
                uploadFileUrl = ""
            }
        })
    }

    override fun apiResponse(data: Any) {

    }
}
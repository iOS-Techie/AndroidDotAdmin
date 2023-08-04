package com.nyotek.dot.admin.ui.fleets.vehicle.detail

import android.app.Application
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSVehicleRepository
import com.nyotek.dot.admin.repository.network.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.repository.network.requests.NSFleetNameUpdateRequest
import com.nyotek.dot.admin.repository.network.requests.NSUpdateCapabilitiesRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleNotesRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleUpdateImageRequest
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.repository.network.responses.NSVehicleResponse
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleAssignBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.VehicleDetailData

class NSVehicleDetailViewModel(application: Application) : NSViewModel(application) {
    var selectedVehicleId: String? = null
    var isEnableVehicle: Boolean = false
    var driverId: String? = null
    var strVehicleDetail: String? = null
    var fleetDetail: String? = null
    var vehicleDataItem: VehicleDataItem? = null
    var fleetModel: FleetData? = null

    fun getVehicleDetail() {
        if (!strVehicleDetail.isNullOrEmpty()) {
            vehicleDataItem = Gson().fromJson(strVehicleDetail, VehicleDataItem::class.java)
            fleetModel = Gson().fromJson(fleetDetail, FleetData::class.java)
        }
    }

    fun updateNotes(notes: String) {
        val request = NSVehicleNotesRequest(vehicleDataItem?.id, notes)
        NSVehicleRepository.updateVehicleNotes(request,this)
    }

    fun updateCapability(list: MutableList<String>) {
        val request = NSUpdateCapabilitiesRequest(vehicleDataItem?.id, list)
        NSVehicleRepository.updateVehicleCapability(request,this)
    }

    fun updateVehicleImage(url: String) {
        val request = NSVehicleUpdateImageRequest(vehicleDataItem?.id, url)
        NSVehicleRepository.updateVehicleImage(request, this)
    }

    fun assignVehicle(driverId: String, vehicleId: String? = vehicleDataItem?.id, capabilities: MutableList<String> = vehicleDataItem?.capabilities?: arrayListOf(), callback: (Boolean) -> Unit) {
        val request = NSAssignVehicleRequest(driverId, fleetModel?.vendorId, vehicleId, capabilities)

        callCommonApi({ obj ->
            NSVehicleRepository.assignVehicle(request, obj)
        }, { _, isSuccess ->
            if (isSuccess) {
                callback.invoke(true)
            }
        })


    }

    /**
     * Get vehicle detail
     *
     * @param isShowProgress
     */
    fun getVehicleDetail(id: String, isShowProgress: Boolean, callback: ((VehicleDetailData) -> Unit)) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            NSVehicleRepository.getVehicleDetail(id, obj)
        }, { data, isSuccess ->
            hideProgress()
            if (isSuccess) {
                if (data is NSVehicleDetailResponse) {
                    callback.invoke(data.vehicleDetailData ?: VehicleDetailData())
                }
            }
        }, false)
    }

    fun vehicleEnableDisable(vehicleId: String?, isEnable: Boolean) {
        if (vehicleId != null) {
            callCommonApi({ obj ->
                NSVehicleRepository.enableDisableVehicle(vehicleId, isEnable, obj)
            }, { _, _ ->

            })
        }
    }

    override fun apiResponse(data: Any) {
        when (data) {
            is NSFleetBlankDataResponse -> {
                branchSuccess()
            }
            is NSVehicleBlankDataResponse -> {
                isProgressShowing.value = false
            }
        }
    }

    private fun branchSuccess() {
        isProgressShowing.value = false
        selectedVehicleId = ""
        isEnableVehicle = false
    }
}
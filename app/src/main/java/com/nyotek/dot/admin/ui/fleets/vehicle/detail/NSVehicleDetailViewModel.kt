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
import com.nyotek.dot.admin.repository.network.responses.NSVehicleBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.VehicleDetailData

class NSVehicleDetailViewModel(application: Application) : NSViewModel(application) {
    var isVehicleDetailAvailable = NSSingleLiveEvent<VehicleDetailData>()
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

    fun assignVehicle(driverId: String) {
        val request = NSAssignVehicleRequest(driverId, fleetModel?.vendorId, vehicleDataItem?.id, vehicleDataItem?.capabilities?: arrayListOf())
        NSVehicleRepository.assignVehicle(request, this)
    }

    /**
     * Get vehicle detail
     *
     * @param isShowProgress
     */
    fun getVehicleDetail(id: String, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSVehicleRepository.getVehicleDetail(id, this)
    }

    override fun apiResponse(data: Any) {
        when (data) {
            is NSVehicleDetailResponse -> {
                isProgressShowing.value = false
                isVehicleDetailAvailable.postValue(data.vehicleDetailData?:VehicleDetailData())
            }
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
package com.nyotek.dot.admin.ui.fleets.employee.detail

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSVehicleRepository
import com.nyotek.dot.admin.repository.network.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.repository.network.requests.NSUpdateCapabilitiesRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleNotesRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleUpdateImageRequest
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleAssignBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.repository.network.responses.VehicleDetailData

class NSDriverDetailViewModel(application: Application) : NSViewModel(application) {
    var isVehicleDetailAvailable = NSSingleLiveEvent<VehicleDetailData>()
    var isVehicleAssign = NSSingleLiveEvent<Boolean>()
    var selectedVehicleId: String? = null
    var isEnableVehicle: Boolean = false
    var driverId: String? = null
    var strVehicleDetail: String? = null
    var fleetDetail: String? = null
    var employeeDataItem: EmployeeDataItem? = null
    var fleetModel: FleetData? = null
    var vehicleDataList: MutableList<VehicleDataItem> = arrayListOf()

    fun getVehicleDetail() {
        if (!strVehicleDetail.isNullOrEmpty()) {
            employeeDataItem = Gson().fromJson(strVehicleDetail, EmployeeDataItem::class.java)
            fleetModel = Gson().fromJson(fleetDetail, FleetData::class.java)
        }
    }

    fun updateNotes(notes: String) {
        val request = NSVehicleNotesRequest(employeeDataItem?.userId, notes)
        NSVehicleRepository.updateVehicleNotes(request,this)
    }

    fun updateCapability(list: MutableList<String>) {
        val request = NSUpdateCapabilitiesRequest(employeeDataItem?.userId, list)
        NSVehicleRepository.updateVehicleCapability(request,this)
    }

    fun updateVehicleImage(url: String) {
        val request = NSVehicleUpdateImageRequest(employeeDataItem?.userId, url)
        NSVehicleRepository.updateVehicleImage(request, this)
    }

    fun assignVehicle(driverId: String, vehicleId: String? = "", capabilities: MutableList<String>) {
        val request = NSAssignVehicleRequest(driverId, fleetModel?.vendorId, vehicleId, capabilities)
        NSVehicleRepository.assignVehicle(request, this)
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
            is NSVehicleAssignBlankDataResponse -> {
                isProgressShowing.value = false
                isVehicleAssign.value = true
            }
        }
    }

    private fun branchSuccess() {
        isProgressShowing.value = false
        selectedVehicleId = ""
        isEnableVehicle = false
    }
}
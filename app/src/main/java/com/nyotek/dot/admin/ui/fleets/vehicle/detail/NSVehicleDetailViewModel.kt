package com.nyotek.dot.admin.ui.fleets.vehicle.detail

import android.app.Application
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.callbacks.NSVehicleEditCallback
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.repository.NSVehicleRepository
import com.nyotek.dot.admin.repository.network.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.repository.network.requests.NSUpdateCapabilitiesRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleDeleteRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleNotesRequest
import com.nyotek.dot.admin.repository.network.requests.NSVehicleUpdateImageRequest
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.VehicleDetailData

class NSVehicleDetailViewModel(application: Application) : NSViewModel(application) {
    var driverId: String? = null
    var vehicleDataItem: VehicleDataItem? = null
    var fleetModel: FleetData? = null

    fun getVehicleDetail(strVehicleDetail: String?, fleetDetail: String?) {
        if (!strVehicleDetail.isNullOrEmpty()) {
            vehicleDataItem = Gson().fromJson(strVehicleDetail, VehicleDataItem::class.java)
            fleetModel = Gson().fromJson(fleetDetail, FleetData::class.java)
        }
    }

    fun updateNotes(notes: String) {
        val request = NSVehicleNotesRequest(vehicleDataItem?.id, notes)
        NSVehicleRepository.updateVehicleNotes(request,this)
    }

    private fun updateCapability(list: MutableList<String>) {
        val request = NSUpdateCapabilitiesRequest(vehicleDataItem?.id, list)
        NSVehicleRepository.updateVehicleCapability(request,this)
    }

    fun updateVehicleImage(url: String) {
        val request = NSVehicleUpdateImageRequest(vehicleDataItem?.id, url)
        NSVehicleRepository.updateVehicleImage(request, this)
    }

    fun assignVehicle(isFromDelete: Boolean, driverId: String, vehicleId: String? = vehicleDataItem?.id, capabilities: MutableList<String> = vehicleDataItem?.capabilities?: arrayListOf(), callback: (Boolean) -> Unit) {
        if (isFromDelete) showProgress()
        val request = NSAssignVehicleRequest(driverId, fleetModel?.vendorId, vehicleId, capabilities)

        callCommonApi({ obj ->
            NSVehicleRepository.assignVehicle(request, obj)
        }, { _, isSuccess ->
            hideProgress()
            if (isSuccess) {
                callback.invoke(true)
            }
        })
    }

    fun deleteVehicle(driverId: String, callback: (Boolean) -> Unit) {
        showProgress()
        val request = NSVehicleDeleteRequest(driverId, fleetModel?.vendorId)

        callCommonApi({ obj ->
            NSVehicleRepository.deleteVehicle(request, obj)
        }, { _, isSuccess ->
            hideProgress()
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
        }, { data, _ ->
            hideProgress()
            if (data is NSVehicleDetailResponse) {
                callback.invoke(data.vehicleDetailData ?: VehicleDetailData())
            } else {
                callback.invoke(VehicleDetailData())
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

    fun updateCapabilityParameter(list: MutableList<String>, capabilityList: MutableList<CapabilitiesDataItem>, callback: NSVehicleEditCallback?) {
        vehicleDataItem?.apply {
            if (capabilities != list) {
                capabilities = list

                capabilityNameList = capabilityList.filter { capabilities.contains(it.id) }
                    .joinToString { getLngValue(it.label) }

                vehicleDataItem?.let { it1 -> callback?.onVehicle(it1) }
                updateCapability(list)
            }
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
    }
}
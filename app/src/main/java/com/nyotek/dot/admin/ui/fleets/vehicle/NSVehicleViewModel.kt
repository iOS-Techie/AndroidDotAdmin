package com.nyotek.dot.admin.ui.fleets.vehicle

import android.app.Application
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.callbacks.NSSuccessFailCallback
import com.nyotek.dot.admin.repository.NSVehicleRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.requests.NSVehicleRequest
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem
import com.nyotek.dot.admin.repository.network.responses.NSVehicleResponse
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSVehicleBlankDataResponse

class NSVehicleViewModel(application: Application) : NSViewModel(application) {
    var isVehicleListAvailable = NSSingleLiveEvent<MutableList<VehicleDataItem>>()
    var ownerId: String? = null
    var strVehicleDetail: String? = null
    var fleetModel: FleetData? = null
    var uploadFileUrl: String? = null

    fun getVehicleDetail() {
        if (!strVehicleDetail.isNullOrEmpty()) {
            fleetModel = Gson().fromJson(strVehicleDetail, FleetData::class.java)
            ownerId = fleetModel?.vendorId
            getVehicleList(true)
        }
    }

    /**
     * Get user detail
     *
     * @param isShowProgress
     */
    fun getVehicleList(isShowProgress: Boolean) {
        if (ownerId != null) {
            if (isShowProgress) {
                isProgressShowing.value = true
            }
            NSVehicleRepository.getVehicleList(ownerId!!, this)
        }
    }

    /**
     * Get user detail
     *
     * @param isShowProgress
     */
    fun vehicleEnableDisable(vehicleId: String?, isEnable: Boolean, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (vehicleId != null) {
            NSVehicleRepository.enableDisableVehicle(vehicleId, isEnable, this)
        } else {
            isProgressShowing.value = false
        }
    }

    fun createVehicle(capabilities: MutableList<String>, detail: HashMap<String, String>, callback: NSSuccessFailCallback){
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

        NSVehicleRepository.createVehicle(request,object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                uploadFileUrl = ""
                //isProgressShowing.value = false
                callback.onResponse(true)
            }

            override fun onError(errors: List<Any>) {
                callback.onResponse(false)
               handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
                callback.onResponse(false)
               handleFailure(failureMessage)
            }

            override fun <T> onNoNetwork(localData: T) {
                callback.onResponse(false)
                handleNoNetwork()
            }

        })
    }

    override fun apiResponse(data: Any) {
        when (data) {
            is NSVehicleResponse -> {
                isProgressShowing.value = false
                isVehicleListAvailable.value = data.data
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
    }
}
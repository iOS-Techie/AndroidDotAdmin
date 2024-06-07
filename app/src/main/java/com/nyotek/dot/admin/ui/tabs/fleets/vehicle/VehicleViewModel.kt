package com.nyotek.dot.admin.ui.tabs.fleets.vehicle

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSVehicleEnableDisableRequest
import com.nyotek.dot.admin.models.requests.NSVehicleRequest
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.NSVehicleResponse
import com.nyotek.dot.admin.models.responses.VehicleDataItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var selectedPosition: Int = -1
    var ownerId: String? = null
    var strVehicleDetail: String? = null
    var fleetModel: FleetData? = null
    var uploadFileUrl: String? = null
    var gson: Gson = Gson()

    fun getVehicleDetail(isShowProgress: Boolean, callback: ((MutableList<VehicleDataItem>) -> Unit)) {
        if (!strVehicleDetail.isNullOrEmpty()) {
            if (fleetModel == null) {
                fleetModel = gson.fromJson(strVehicleDetail, FleetData::class.java)
            }
            ownerId = fleetModel?.vendorId
            if (ownerId?.isNotEmpty() == true) {
                getCapabilities(isShowProgress, ownerId!!, callback) {}
            } else {
                callback.invoke(arrayListOf())
            }
        }
    }

    fun getCapabilities(isShowProgress: Boolean, id: String? = null, callback: ((MutableList<VehicleDataItem>) -> Unit)? = null, capCallback: ((MutableList<CapabilitiesDataItem>) -> Unit?)) {
        if (isShowProgress) showProgress()
        getCapabilities(false, isApiDataCheck = false, callback = { capabilitiesList ->
            if (id != null && callback != null) {
                getVehicleList(false, id, capabilitiesList, false, callback)
            } else {
                hideProgress()
                capCallback.invoke(arrayListOf())
            }
        })
    }

    fun getVehicleList(isShowProgress: Boolean, refId: String, list: MutableList<CapabilitiesDataItem>, isFromDriverDetail: Boolean, callback: (MutableList<VehicleDataItem>) -> Unit) = viewModelScope.launch{
        getVehicleListApi(isShowProgress, refId, list, isFromDriverDetail, callback)
    }

    private suspend fun getVehicleListApi(isShowProgress: Boolean, refId: String, list: MutableList<CapabilitiesDataItem>, isFromDriverDetail: Boolean, callback: (MutableList<VehicleDataItem>) -> Unit) {
        if (isShowProgress) showProgress()
        performApiCalls({ repository.remote.vehicleList(refId)
        }) { response, isSuccess ->
            if (isSuccess) {
                if (!isFromDriverDetail) {
                    hideProgress()
                }
                val data = response.first() as NSVehicleResponse?
                if (data is NSVehicleResponse) {
                    data.data.sortBy { it.id }

                    for (vehicle in data.data) {
                        vehicle.capabilityNameList =
                            list.filter { vehicle.capabilities.contains(it.id) }
                                .joinToString { getLngValue(it.label) }
                    }

                    callback.invoke(data.data)
                }
            } else {
                hideProgress()
            }
        }
    }


    fun vehicleEnableDisable(vehicleId: String?, isEnable: Boolean) = viewModelScope.launch{
        vehicleEnableDisableApi(vehicleId, isEnable)
    }

    private suspend fun vehicleEnableDisableApi(vehicleId: String?, isEnable: Boolean) {
        if (vehicleId != null) {
            performApiCalls({
                if (isEnable) {
                    repository.remote.enableVehicle(NSVehicleEnableDisableRequest(vehicleId))
                } else {
                    repository.remote.disableVehicle(NSVehicleEnableDisableRequest(vehicleId))
                }
            }) {_, _ ->}
        }
    }

    fun createVehicle(capabilities: MutableList<String>, detail: HashMap<String, String>, callback: ((Boolean) -> Unit)) = viewModelScope.launch{
        createVehicleApi(capabilities, detail, callback)
    }

    private suspend fun createVehicleApi(capabilities: MutableList<String>, detail: HashMap<String, String>, callback: ((Boolean) -> Unit)) {
        showProgress()
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

        performApiCalls({
            repository.remote.createVehicle(request)
        }) { _, isSuccess ->
            hideProgress()
            if (isSuccess) {
                uploadFileUrl = ""
                callback.invoke(true)
            }
        }
    }
}
package com.nyotek.dot.admin.ui.tabs.fleets.vehicle.detail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.callbacks.NSVehicleEditCallback
import com.nyotek.dot.admin.common.extension.getLngValue
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSAssignVehicleRequest
import com.nyotek.dot.admin.models.requests.NSUpdateCapabilitiesRequest
import com.nyotek.dot.admin.models.requests.NSVehicleDeleteRequest
import com.nyotek.dot.admin.models.requests.NSVehicleEnableDisableRequest
import com.nyotek.dot.admin.models.requests.NSVehicleNotesRequest
import com.nyotek.dot.admin.models.requests.NSVehicleUpdateImageRequest
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.NSVehicleDetailResponse
import com.nyotek.dot.admin.models.responses.VehicleDataItem
import com.nyotek.dot.admin.models.responses.VehicleDetailData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VehicleDetailViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var driverId: String? = null
    var vehicleDataItem: VehicleDataItem? = null
    var fleetModel: FleetData? = null

    fun getVehicleDetail(strVehicleDetail: String?, fleetDetail: String?) {
        if (!strVehicleDetail.isNullOrEmpty()) {
            vehicleDataItem = Gson().fromJson(strVehicleDetail, VehicleDataItem::class.java)
            fleetModel = Gson().fromJson(fleetDetail, FleetData::class.java)
        }
    }

    fun updateNotes(notes: String) = viewModelScope.launch{
        updateNotesApi(notes)
    }

    private suspend fun updateNotesApi(notes: String) {
        val request = NSVehicleNotesRequest(vehicleDataItem?.id, notes)
        performApiCalls({ repository.remote.updateVehicleNotes(request)
        }) { _, _ ->
            hideProgress()
        }
    }

    fun updateCapability(list: MutableList<String>) = viewModelScope.launch{
        updateCapabilityApi(list)
    }

    private suspend fun updateCapabilityApi(list: MutableList<String>) {
        val request = NSUpdateCapabilitiesRequest(vehicleDataItem?.id, list)
        performApiCalls({ repository.remote.updateVehicleCapability(request)
        }) { _, _ ->
            hideProgress()
        }
    }

    fun updateVehicleImage(url: String) = viewModelScope.launch{
        updateVehicleImageApi(url)
    }

    private suspend fun updateVehicleImageApi(url: String) {
        val request = NSVehicleUpdateImageRequest(vehicleDataItem?.id, url)
        performApiCalls({ repository.remote.vehicleUpdateImage(request)
        }) { _, _ ->
            hideProgress()
        }
    }

    fun assignVehicle(isFromDelete: Boolean, driverId: String, vehicleId: String? = vehicleDataItem?.id, capabilities: MutableList<String> = vehicleDataItem?.capabilities?: arrayListOf(), callback: (Boolean) -> Unit) = viewModelScope.launch{
        assignVehicleApi(isFromDelete, driverId, vehicleId, capabilities, callback)
    }

    private suspend fun assignVehicleApi(isFromDelete: Boolean, driverId: String, vehicleId: String? = vehicleDataItem?.id, capabilities: MutableList<String> = vehicleDataItem?.capabilities?: arrayListOf(), callback: (Boolean) -> Unit) {
        if (isFromDelete) showProgress()
        val request = NSAssignVehicleRequest(driverId, fleetModel?.vendorId, vehicleId, capabilities)

        performApiCalls({ repository.remote.assignVehicle(request)
        }) { _, _ ->
            hideProgress()
            callback.invoke(true)
        }
    }

    fun deleteVehicle(driverId: String, callback: (Boolean) -> Unit) = viewModelScope.launch{
        deleteVehicleApi(driverId, callback)
    }

    private suspend fun deleteVehicleApi(driverId: String, callback: (Boolean) -> Unit) {
        showProgress()
        val request = NSVehicleDeleteRequest(driverId, fleetModel?.vendorId)

        performApiCalls({ repository.remote.deleteVehicle(request)
        }) { _, _ ->
            hideProgress()
            callback.invoke(true)
        }
    }

    fun getVehicleDetail(id: String, isShowProgress: Boolean, callback: ((VehicleDetailData) -> Unit)) = viewModelScope.launch{
        getVehicleDetailApi(id, isShowProgress, callback)
    }

    private suspend fun getVehicleDetailApi(id: String, isShowProgress: Boolean, callback: ((VehicleDetailData) -> Unit)) {
        if (isShowProgress) showProgress()

        performApiCalls({ repository.remote.getVehicleDetail(id)
        }) { response, isSuccess ->
            if (isSuccess) {
                val data = response.first() as NSVehicleDetailResponse?
                if (data is NSVehicleDetailResponse) {
                    callback.invoke(data.vehicleDetailData ?: VehicleDetailData())
                } else {
                    callback.invoke(VehicleDetailData())
                }
            } else {
                callback.invoke(VehicleDetailData())
            }
        }
        //showError false
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

            }) { _, _ ->

            }
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
}
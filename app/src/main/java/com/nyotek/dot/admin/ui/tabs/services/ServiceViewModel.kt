package com.nyotek.dot.admin.ui.tabs.services

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSServiceCapabilitiesRequest
import com.nyotek.dot.admin.models.requests.NSServiceFleetUpdateRequest
import com.nyotek.dot.admin.models.requests.NSServiceRequest
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.NSGetServiceListData
import com.nyotek.dot.admin.models.responses.NSServiceCapabilityResponse
import com.nyotek.dot.admin.models.responses.ServiceCapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.ServiceMainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ServiceViewModel @Inject constructor(
    val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var serviceMainList: MutableLiveData<ServiceMainModel> = MutableLiveData()
    var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()

    fun getServiceList(isShowProgress: Boolean) = viewModelScope.launch {
        if (isShowProgress) showProgress()
        getBaseServiceList(!isShowProgress) {
            removeItemById(NSThemeHelper.SERVICE_ID, it?.data?: arrayListOf())
            it?.data?.sortBy { id ->  id.serviceId }
            getCapabilities(false, callback = { capabilitiesList ->
                getFleets { fleetList ->
                    serviceMainList.postValue(ServiceMainModel(it?.data, capabilitiesList, fleetList))
                    hideProgress()
                }
            })
        }
    }

    private fun removeItemById(idToRemove: String, modelList: MutableList<NSGetServiceListData>) {
        val iterator = modelList.iterator()
        while (iterator.hasNext()) {
            val model = iterator.next()
            if (model.serviceId == idToRemove) {
                iterator.remove()
            }
        }
    }


    fun serviceEnableDisable(isEnable: Boolean, serviceId: String) = viewModelScope.launch {
        serviceEnableDisableApi(isEnable, serviceId)
    }

    private suspend fun serviceEnableDisableApi(isEnable: Boolean, serviceId: String) {
        performApiCalls(
            {
                if (isEnable) {
                    repository.remote.enableService(NSServiceRequest(serviceId))
                } else {
                    repository.remote.disableService(NSServiceRequest(serviceId))
                }
            }
        ) {_, _ ->}
    }

    fun serviceFleetAddOrDelete(serviceId: String, fleetId: String, isAdd: Boolean) = viewModelScope.launch {
        serviceFleetAddOrDeleteApi(serviceId, fleetId, isAdd)
    }

    private suspend fun serviceFleetAddOrDeleteApi(
        serviceId: String,
        fleetId: String,
        isAdd: Boolean
    ) {
        showProgress()
        performApiCalls(
            {
                if (isAdd) {
                    repository.remote.assignedServiceFleets(
                        NSServiceFleetUpdateRequest(
                            serviceId,
                            fleetId
                        )
                    )
                } else {
                    repository.remote.deleteAssignedServiceFleets(serviceId, fleetId)
                }
            }
        ) { _, _ ->
            hideProgress()
        }
    }

    fun serviceCapabilityUpdate(serviceId: String, capabilityId: String) = viewModelScope.launch {
        serviceCapabilityUpdateApi(serviceId, capabilityId)
    }

    private suspend fun serviceCapabilityUpdateApi(serviceId: String, capabilityId: String) {
        performApiCalls(
            { repository.remote.updateServiceCapability(NSServiceCapabilitiesRequest(serviceId, capabilityId)) }
        ) { _, _ ->
        }
    }

    fun getServiceCapability(serviceId: String?, callback: (ServiceCapabilitiesDataItem?) -> Unit) = viewModelScope.launch {
        getServiceCapabilityApi(serviceId, callback)
    }
    private suspend fun getServiceCapabilityApi(serviceId: String?, callback: (ServiceCapabilitiesDataItem?) -> Unit) {
        if (serviceId != null) {
            performApiCalls(
                { repository.remote.getServiceCapability(serviceId) }
            ) { responses, isSuccess ->
                if (isSuccess) {
                    val response = responses[0] as NSServiceCapabilityResponse?
                    colorResources.themeHelper.setCapabilityItemList(serviceId, response?.data)
                    callback.invoke(response?.data)
                } else {
                    callback.invoke(null)
                }
            }
        } else {
            callback.invoke(null)
        }
    }
}
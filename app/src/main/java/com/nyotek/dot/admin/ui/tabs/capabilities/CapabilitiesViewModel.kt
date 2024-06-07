package com.nyotek.dot.admin.ui.tabs.capabilities

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSCapabilitiesRequest
import com.nyotek.dot.admin.models.requests.NSCreateCapabilityRequest
import com.nyotek.dot.admin.models.requests.NSLanguageRequest
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.NSCapabilitiesResponse
import com.nyotek.dot.admin.models.responses.NSLocalLanguageResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CapabilitiesViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()

    fun capabilitiesEnableDisable(isEnable: Boolean, capabilitiesId: String) = viewModelScope.launch {
        capabilitiesEnableDisableApi(isEnable, capabilitiesId)
    }

    private suspend fun capabilitiesEnableDisableApi(isEnable: Boolean, capabilitiesId: String) {
        performApiCalls(
            {
                if (isEnable) {
                    repository.remote.enableCapabilities(NSCapabilitiesRequest(capabilitiesId))
                } else {
                    repository.remote.disableCapabilities(NSCapabilitiesRequest(capabilitiesId))
                }
            }
        ) { _, _ -> }
    }

    fun capabilitiesDelete(capabilitiesId: String) = viewModelScope.launch {
        capabilitiesDeleteApi(capabilitiesId)
    }

    private suspend fun capabilitiesDeleteApi(capabilitiesId: String) {
        showProgress()
        performApiCalls(
            { repository.remote.capabilityDelete(capabilitiesId) }
        ) { _, isSuccess ->
            if (isSuccess) {
                getCapabilities(false, isApiDataCheck = true)
            }
        }
    }

    fun createEditCapability(capabilityName: HashMap<String, String>,
                             isCreate: Boolean,
                             selectedId: String = "", callback: ((MutableList<CapabilitiesDataItem>) -> Unit)? = null) = viewModelScope.launch {
        createEditCapabilityApi(capabilityName, isCreate, selectedId, callback)
    }

    private suspend fun createEditCapabilityApi(
        capabilityName: HashMap<String, String>,
        isCreate: Boolean,
        selectedId: String = "", callback: ((MutableList<CapabilitiesDataItem>) -> Unit)? = null)
    {
        showProgress()
        val request = NSCreateCapabilityRequest()
        request.label = capabilityName

        if (isCreate) {
            performApiCalls(
                { repository.remote.createCapability(request) }
            ) { _, isSuccess ->
                if (isSuccess) {
                    getCapabilities(false, isApiDataCheck = true, callback)
                }
            }
        } else {
            performApiCalls(
                { repository.remote.updateCapability(selectedId, request) }
            ) { _, isSuccess ->
                if(isSuccess){
                    getCapabilities(false, isApiDataCheck = true, callback)
                }
            }
        }
    }
}
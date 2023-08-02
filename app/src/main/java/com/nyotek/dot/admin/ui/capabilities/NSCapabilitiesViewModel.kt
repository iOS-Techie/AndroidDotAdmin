package com.nyotek.dot.admin.ui.capabilities

import android.app.Application
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.repository.NSCapabilitiesRepository
import com.nyotek.dot.admin.repository.NSLanguageRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.requests.NSCreateCapabilityRequest
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.NSCapabilitiesBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSCapabilitiesResponse
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse

class NSCapabilitiesViewModel(application: Application) : NSViewModel(application) {

    /**
     * Get Capabilities List
     *
     * @param isShowProgress
     * @param isShowError
     * @param isCapabilityCheck
     * @return callback
     */
    fun getCapabilities(
        isShowProgress: Boolean,
        isShowError: Boolean = true,
        isCapabilityCheck: Boolean = false,
        callback: ((MutableList<CapabilitiesDataItem>) -> Unit?)
    ) {
        if (isShowProgress) showProgress()
        getCapabilitiesList(isShowError = isShowError, isCapabilityCheck = isCapabilityCheck) {
            hideProgress()
            callback.invoke(it)
        }
    }

    /**
     * Capabilities enable disable
     *
     * @param isShowProgress
     */
    fun capabilityEnableDisable(
        capabilitiesId: String?,
        isEnable: Boolean,
        isShowProgress: Boolean
    ) {
        if (capabilitiesId != null) {
            if (isShowProgress) showProgress()
            callCommonApi({ obj ->
                NSCapabilitiesRepository.enableDisableCapabilities(capabilitiesId, isEnable, obj)
            }, { _, _ ->
                hideProgress()
            })
        }
    }

    /**
     * Capabilities Delete
     *
     * @param isShowProgress
     */
    fun capabilitiesDelete(id: String, callback: ((MutableList<CapabilitiesDataItem>) -> Unit?)) {
        showProgress()
        callCommonApi({ obj ->
            NSCapabilitiesRepository.deleteCapability(id, obj)
        }, { _, _ ->
            getCapabilities(isShowProgress = true, callback = callback)
        })
    }

    fun createEditCapability(
        capabilityName: HashMap<String, String>,
        isCreate: Boolean,
        selectedId: String = "",
        callback: ((MutableList<CapabilitiesDataItem>) -> Unit?)
    ) {
        val request = NSCreateCapabilityRequest()
        request.label = capabilityName

        showProgress()
        callCommonApi({ obj ->
            NSCapabilitiesRepository.createEditCapability(isCreate, selectedId, request, obj)
        }, { _, isSuccess ->
            if (!isSuccess) {
                hideProgress()
                callback.invoke(arrayListOf())
            } else {
                getCapabilities(isShowProgress = false, isCapabilityCheck = false, callback = callback)
            }
        })
    }

    override fun apiResponse(data: Any) {
        when (data) {
            is NSCapabilitiesResponse -> {

            }
        }
    }
}
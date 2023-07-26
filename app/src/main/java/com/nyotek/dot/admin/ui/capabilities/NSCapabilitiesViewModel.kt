package com.nyotek.dot.admin.ui.capabilities

import android.app.Application
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.callbacks.NSCapabilityCallback
import com.nyotek.dot.admin.common.callbacks.NSSuccessFailCallback
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
    var isCapabilitiesListCall = NSSingleLiveEvent<MutableList<CapabilitiesDataItem>>()
    var selectedCapabilities: CapabilitiesDataItem? = null

    fun getLocalLanguageList() {
        NSLanguageRepository.localLanguages("", this)
    }

    /**
     * Get Capabilities List
     *
     * @param isShowProgress
     */
    fun getCapabilitiesList(isShowProgress: Boolean, isShowError: Boolean = true, isCapabilityAvailableCheck: Boolean = false, callback: NSCapabilityCallback? = null) {

        if (!isCapabilityAvailable() || !isCapabilityAvailableCheck) {
            if (isShowProgress) {
                isProgressShowing.value = true
            }
            NSCapabilitiesRepository.getCapabilities(object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    if (data is NSCapabilitiesResponse) {
                        data.data.sortBy { it.id }
                        isCapabilitiesListCall.value = data.data
                        NSApplication.getInstance().setCapabilityList(data.data)
                        isProgressShowing.value = false
                        callback?.onCapability(data.data)
                    }
                }

                override fun onError(errors: List<Any>) {
                    if (isShowError) {
                        handleError(errors)
                    }
                }

                override fun onFailure(failureMessage: String?) {
                    if (isShowError) {
                        handleFailure(failureMessage)
                    }
                }

                override fun <T> onNoNetwork(localData: T) {
                    if (isShowError) {
                        handleNoNetwork()
                    }
                }

            })
        } else {
            val capabilityList = NSApplication.getInstance().getCapabilityList()
            isCapabilitiesListCall.value = capabilityList
            isProgressShowing.value = false
            callback?.onCapability(capabilityList)
        }
    }

    private fun isCapabilityAvailable(): Boolean {
        return NSApplication.getInstance().getCapabilityList().isValidList()
    }

    /**
     * Capabilities enable disable
     *
     * @param isShowProgress
     */
    fun capabilityEnableDisable(capabilitiesId: String?, isEnable: Boolean, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (capabilitiesId != null) {
            NSCapabilitiesRepository.enableDisableCapabilities(capabilitiesId, isEnable, this)
        } else {
            isProgressShowing.value = false
        }
    }

    /**
     * Capabilities Delete
     *
     * @param isShowProgress
     */
    fun capabilitiesDelete(id: String, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSCapabilitiesRepository.deleteCapability(id, this)
    }

    fun createEditCapability(capabilityName: HashMap<String, String>, isCreate: Boolean, selectedId: String = "", callback: NSSuccessFailCallback){
        isProgressShowing.value = true
        val request = NSCreateCapabilityRequest()
        request.label = capabilityName
        val obj = object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
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
        }

        NSCapabilitiesRepository.createEditCapability(isCreate, selectedId, request, obj)
    }



    override fun apiResponse(data: Any) {
        when (data) {
            is NSCapabilitiesResponse -> {

            }
            is NSFleetBlankDataResponse -> {
                isProgressShowing.value = false
            }
            is NSCapabilitiesBlankDataResponse -> {
                //employeeEditRequest = NSEmployeeEditRequest()
                getCapabilitiesList(true)
            }
            is NSLocalLanguageResponse -> {
                if (data.data.isValidList()) {
                    data.data[0].isSelected = true
                }
                NSApplication.getInstance().setFleetLanguageList(data.data)
            }
        }
    }
}
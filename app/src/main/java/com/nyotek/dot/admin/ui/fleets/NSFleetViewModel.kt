package com.nyotek.dot.admin.ui.fleets

import android.app.Application
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.NSLanguageRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetListResponse
import com.nyotek.dot.admin.repository.network.responses.NSBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSCreateCompanyResponse
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse

class NSFleetViewModel(application: Application) : NSViewModel(application) {
    var fleetItemList: MutableList<FleetData> = arrayListOf()
    var isFleetListAvailable = NSSingleLiveEvent<Boolean>()
    var createCompanyRequest: NSCreateCompanyRequest = NSCreateCompanyRequest()
    var urlToUpload: String = ""
    var selectedFilterList: MutableList<ActiveInActiveFilter> = arrayListOf()

    /**
     * Get Fleet list
     *
     * @param isShowProgress
     */
    fun getFleetList(isShowProgress: Boolean, callback: ((MutableList<FleetData>) -> Unit)? = null) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSFleetRepository.getFleetList(object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                if (data is FleetListResponse) {
                    fleetItemList.clear()
                    if (data.data.isValidList()) {
                        fleetItemList.addAll(data.data)
                        fleetItemList.sortByDescending { it.vendorId }
                    }
                    if (callback == null) {
                        getFleetLocalLanguageList(false)
                    } else {
                        callback.invoke(fleetItemList)
                    }
                }
            }

            override fun onError(errors: List<Any>) {
                handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
                handleFailure(failureMessage)
            }

            override fun <T> onNoNetwork(localData: T) {
                handleNoNetwork()
            }

        })
    }

    /**
     * enable disable fleet
     *
     * @param isShowProgress
     */
    fun fleetEnableDisable(fleetId: String?, isEnable: Boolean, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (fleetId != null) {
            NSFleetRepository.enableDisableFleet(fleetId, isEnable, this)
        } else {
            isProgressShowing.value = false
        }
    }

    fun createFleet() {
        isProgressShowing.value = true
        NSFleetRepository.createFleet(createCompanyRequest, this)
    }

    private fun getFleetLocalLanguageList(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSLanguageRepository.localLanguages("", this)
    }

    override fun apiResponse(data: Any) {
        when (data) {
            is NSCreateCompanyResponse -> {
                createCompanyRequest = NSCreateCompanyRequest()
                urlToUpload = ""
                getFleetList(true)
            }
            is NSBlankDataResponse -> {
                isProgressShowing.value = false
            }
            is NSFleetBlankDataResponse -> {
                isProgressShowing.value = false
            }
            is NSLocalLanguageResponse -> {
                isProgressShowing.value = false
                if (data.data.isValidList()) {
                    data.data[0].isSelected = true
                }
                NSApplication.getInstance().setFleetLanguageList(data.data)
                isFleetListAvailable.value = fleetItemList.isValidList()
            }
        }
    }
}
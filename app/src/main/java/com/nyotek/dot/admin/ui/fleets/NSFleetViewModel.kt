package com.nyotek.dot.admin.ui.fleets

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.ui.dispatch.detail.NSDispatchViewRepository
import com.nyotek.dot.admin.ui.dispatch.detail.NSDispatchViewRepository.getDispatchDetailAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NSFleetViewModel(application: Application) : NSViewModel(application) {
    var createCompanyRequest: NSCreateCompanyRequest = NSCreateCompanyRequest()
    var urlToUpload: String = ""
    var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()

    /**
     * Get Fleet list
     *
     * @param isShowProgress
     */
    fun getFleetList(isShowProgress: Boolean, callback: ((MutableList<FleetData>) -> Unit)) {
        if (isShowProgress) showProgress()
        getFleetListApi { fleets ->
            getLocalLanguages(isSelect = true) {
                hideProgress()
                callback.invoke(fleets)
            }
        }
    }

    /**
     * enable disable fleet
     *
     */
    fun fleetEnableDisable(fleetId: String?, isEnable: Boolean) {
        callCommonApi({ obj ->
            if (fleetId != null) {
                NSFleetRepository.enableDisableFleet(fleetId, isEnable, obj)
            }
        }, { _, _ ->

        })
    }

    fun createFleet(callback: ((MutableList<FleetData>) -> Unit)) {
        showProgress()
        callCommonApi({ obj ->
            NSFleetRepository.createFleet(createCompanyRequest, obj)
        }, { _, isSuccess ->
            hideProgress()
            if (isSuccess) {
                createCompanyRequest = NSCreateCompanyRequest()
                urlToUpload = ""
                getFleetList(true, callback)
            }
        })
    }

    override fun apiResponse(data: Any) {

    }
}
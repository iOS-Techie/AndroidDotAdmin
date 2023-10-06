package com.nyotek.dot.admin.ui.dispatch

import android.app.Application
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSDispatchRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListResponse
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse

class NSDispatchViewModel(application: Application) : NSViewModel(application) {
    var createCompanyRequest: NSCreateCompanyRequest = NSCreateCompanyRequest()
    var urlToUpload: String = ""
    var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()
    var selectedServiceId: String? = null
    var selectedServiceLogo: String? = null

    fun getServiceListApi(isShowProgress: Boolean, callback: ((MutableList<NSGetServiceListData>) -> Unit)) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            NSServiceRepository.getServiceList(obj)
        }, { data, _ ->
            if (data is NSGetServiceListResponse) {
                data.data.sortBy { it.serviceId }
                val list = data.data.filter { it.isActive } as MutableList<NSGetServiceListData>
                if (list.isEmpty()) {
                    hideProgress()
                }
                callback.invoke(list)
            } else {
                hideProgress()
            }
        })
    }

    fun getDispatchFromService(
        serviceId: String?,
        isShowProgress: Boolean,
        callback: ((MutableList<NSDispatchOrderListData>) -> Unit?)
    ) {
        if (isShowProgress) showProgress()

        callCommonApi({ obj ->
            NSDispatchRepository.getDispatchFromService(serviceId!!, obj)
        }, { data, _ ->
            hideProgress()
            if (data is NSDispatchOrderListResponse) {
                data.orderData.sortByDescending { NSDateTimeHelper.getCommonDateView(it.status.first().statusCapturedTime) }
                callback.invoke(data.orderData)
            }
        })
    }

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
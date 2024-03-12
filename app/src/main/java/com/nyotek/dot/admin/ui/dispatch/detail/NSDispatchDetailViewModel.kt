package com.nyotek.dot.admin.ui.dispatch.detail

import android.app.Application
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.network.responses.DocumentDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.NSDispatchDetailAllResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse

class NSDispatchDetailViewModel(application: Application) : NSViewModel(application) {

    var dispatchSelectedData: NSDispatchOrderListData? = null
    var isMapReset: Boolean = false
    var currentMapFleetData: FleetDataItem? = null

    fun getDispatchDetail(strDispatch: String?, callback: ((NSDispatchDetailAllResponse) -> Unit)) {
        if (strDispatch?.isNotEmpty() == true) {
            dispatchSelectedData = Gson().fromJson(strDispatch, NSDispatchOrderListData::class.java)
            if (dispatchSelectedData?.dispatchId?.isNotEmpty() == true) {
                getDispatchDetail(dispatchSelectedData?.dispatchId?:"", dispatchSelectedData?.vendorId?:"", callback)
            }
        }
    }

    private fun getDispatchDetail(dispatchId: String, vendorId: String, callback: ((NSDispatchDetailAllResponse) -> Unit)) {
        showProgress()
        callCommonApi({ obj ->
            NSDispatchViewRepository.getDispatchLocationHistory(dispatchId, vendorId, obj)
        }, { data, _ ->
            hideProgress()
            if (data is NSDispatchDetailAllResponse) {
                callback.invoke(data)
            }
        })
    }

    fun getDriverDetail(list: MutableList<DocumentDataItem>?): DocumentDataItem? {
        return list?.find { it.documentType == "Driving licence" }
    }

    fun getServiceLogo(serviceId: String?, callback: (String?) -> Unit) {
        if (serviceId?.isNotEmpty() == true) {
            callCommonApi({ obj ->
                NSServiceRepository.getServiceList(obj)
            }, { data, _ ->
                if (data is NSGetServiceListResponse) {
                    callback.invoke(data.data.find { it.serviceId == serviceId }?.logoUrl)
                }
            })
        }
    }

    override fun apiResponse(data: Any) {

    }
}
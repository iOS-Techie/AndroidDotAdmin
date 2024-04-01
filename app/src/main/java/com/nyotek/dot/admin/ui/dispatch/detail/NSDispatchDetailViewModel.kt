package com.nyotek.dot.admin.ui.dispatch.detail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.network.responses.DocumentDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.NSBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchDetailAllResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.repository.network.responses.NSDocumentListResponse
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.repository.network.responses.VendorDetailResponse
import kotlinx.coroutines.launch

class NSDispatchDetailViewModel(application: Application) : NSViewModel(application) {

    var dispatchSelectedData: NSDispatchOrderListData? = null
    var isMapReset: Boolean = false
    var currentMapFleetData: FleetDataItem? = null
    var selectedServiceId: String? = null

    fun getDispatchFromList(strDispatch: String?) {
        if (strDispatch?.isNotEmpty() == true) {
            dispatchSelectedData = Gson().fromJson(strDispatch, NSDispatchOrderListData::class.java)
        }
    }
    fun getDispatchDetail(callback: (NSDispatchDetailAllResponse) -> Unit) {
        if (dispatchSelectedData != null) {
            if (dispatchSelectedData?.dispatchId?.isNotEmpty() == true) {
                getDispatchDetail(dispatchSelectedData?.dispatchId?:"", dispatchSelectedData?.vendorId?:"", dispatchSelectedData?.assignedDriverId?:"", dispatchSelectedData?.isThirdParty?:false, callback)
            }
        }
    }

    private fun getDispatchDetail(dispatchId: String, vendorId: String, driverId: String, isThirdParty: Boolean, callback: ((NSDispatchDetailAllResponse) -> Unit)) {
        showProgress()
        callCommonApi({ obj ->
            NSDispatchViewRepository.getDispatchLocationHistory(dispatchId, vendorId, driverId, selectedServiceId, isThirdParty, obj)
        }, { data, _ ->
            hideProgress()
            if (data is NSDispatchDetailAllResponse) {
                if (isThirdParty) {
                    //when third party true then use from dispatch list list
                    data.vendorDetail = VendorDetailResponse(name = dispatchSelectedData?.vendorName, logo = dispatchSelectedData?.vendorLogoUrl, logoScale = "fit")
                }
                callback.invoke(data)
            }
        })
    }

    fun getDriverDocumentsStrings(serviceId: String, callback: ((NSDocumentListResponse) -> Unit)) {
        callCommonApi({ obj ->
            viewModelScope.launch {
                NSDispatchViewRepository.getDriverDocument(
                    serviceId,
                    viewModelCallback = obj
                )
            }
        }, { data, _ ->
            if (data is NSDocumentListResponse) {
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


    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    // Cancel Order
    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    fun updateOrderStatus(dispatchId: String, status: String, callback: (() -> Unit)) {
        showProgress()
        callCommonApi({ obj ->
            NSDispatchViewRepository.updateDispatchOrderStatus(dispatchId, status, obj)
        }, { data, isSuccess ->
            hideProgress()
            if (data is NSBlankDataResponse) {
                if (isSuccess) {
                    callback.invoke()
                }
            }
        })
    }

    fun assignDriver(dispatchId: String, driverId: String, callback: (() -> Unit)) {
        showProgress()
        callCommonApi({ obj ->
            NSDispatchViewRepository.assignDriver(dispatchId, driverId, obj)
        }, { data, isSuccess ->
            hideProgress()
            if (data is NSBlankDataResponse) {
                if (isSuccess) {
                    callback.invoke()
                }
            }
        })
    }

    override fun apiResponse(data: Any) {

    }
}
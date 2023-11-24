package com.nyotek.dot.admin.ui.dispatch.detail

import android.app.Application
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSDispatchRepository
import com.nyotek.dot.admin.repository.NSFleetRepository
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.NSVehicleRepository
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.DispatchDetailResponse
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetLocationResponse
import com.nyotek.dot.admin.repository.network.responses.NSAssignVehicleDriverResponse
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import com.nyotek.dot.admin.repository.network.responses.NSDriverVehicleDetailResponse
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.repository.network.responses.VehicleData

class NSDispatchDetailViewModel(application: Application) : NSViewModel(application) {
    var createCompanyRequest: NSCreateCompanyRequest = NSCreateCompanyRequest()
    var urlToUpload: String = ""
    var filterList: MutableList<ActiveInActiveFilter> = arrayListOf()
    var selectedServiceId: String? = null
    var dispatchSelectedData: NSDispatchOrderListData? = null

    fun getDispatchDetail(strDispatch: String?, callback: ((VehicleData?) -> Unit)) {
        if (strDispatch?.isNotEmpty() == true) {
            dispatchSelectedData = Gson().fromJson(strDispatch, NSDispatchOrderListData::class.java)
            if (dispatchSelectedData?.dispatchId?.isNotEmpty() == true) {
               // getAssignVehicleDriver(dispatchSelectedData?.assignedDriverId, dispatchSelectedData?.vendorId?:"", true, callback)
                getLocationHistory(true, dispatchSelectedData!!.dispatchId!!, callback)
            }
        }
    }

    fun getAssignVehicleDriver(driverId: String?, fleetId: String, isShowProgress: Boolean, callback: ((VehicleData?) -> Unit)) {
        if (driverId != null) {
            if (isShowProgress) showProgress()
            callCommonApi({ obj ->
                NSVehicleRepository.getAssignVehicleDriver(driverId, fleetId, obj)
            }, { data, isSuccess ->
                if (!isSuccess) {
                    hideProgress()
                }
                if (data is NSAssignVehicleDriverResponse) {
                    getDriverVehicleDetail(data.data?.vehicleId, callback)
                } else {
                    callback.invoke(VehicleData())
                    hideProgress()
                }
            })
        }
    }

    private fun getLocationHistory(isShowProgress: Boolean, dispatchId: String, callback: ((VehicleData?) -> Unit)) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            NSDispatchRepository.getDispatchLocationHistory(dispatchId, obj)
        }, { data, _ ->
            if (data is FleetLocationResponse) {
                val features = data.fleetDataItem?.features?: arrayListOf()
                if (features.isNotEmpty()) {
                    val location = features.first().properties
                    if (location != null) {
                        getDriverVehicleDetail(location.vehicleId, callback)
                    } else {
                        callback.invoke(VehicleData())
                        hideProgress()
                    }
                } else {
                    callback.invoke(VehicleData())
                    hideProgress()
                }
            } else {
                callback.invoke(VehicleData())
                hideProgress()
            }
        })
    }

    private fun getDriverVehicleDetail(vehicleId: String?, callback: ((VehicleData?) -> Unit)) {
        if (vehicleId != null) {
            showProgress()
            callCommonApi({ obj ->
                NSVehicleRepository.getDriverVehicleDetail(vehicleId, obj)
            }, { data, _ ->
                hideProgress()
                if (data is NSDriverVehicleDetailResponse) {
                    callback.invoke(data.data)
                } else {
                    callback.invoke(VehicleData())
                }
            }, false)
        }
    }

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

//    fun getDispatchFromService(
//        dispatchId: String?,
//        isShowProgress: Boolean,
//        callback: ((MutableList<NSDispatchOrderListData>) -> Unit?)
//    ) {
//        if (isShowProgress) showProgress()
//
//        callCommonApi({ obj ->
//            NSDispatchRepository.getDispatchDetail(dispatchId!!, obj)
//        }, { data, _ ->
//            hideProgress()
//            if (data is DispatchDetailResponse) {
//                /*data.orderData.sortByDescending { NSDateTimeHelper.getCommonDateView(it.status.first().statusCapturedTime) }
//                callback.invoke(data.orderData)*/
//            }
//        })
//    }

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
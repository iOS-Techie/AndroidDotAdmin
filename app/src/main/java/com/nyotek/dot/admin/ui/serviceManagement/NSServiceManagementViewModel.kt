package com.nyotek.dot.admin.ui.serviceManagement

import android.app.Application
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.repository.NSCapabilitiesRepository
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.repository.network.responses.NSServiceCapabilityResponse
import com.nyotek.dot.admin.repository.network.responses.ServiceCapabilitiesDataItem

class NSServiceManagementViewModel(application: Application) : NSViewModel(application) {

    /**
     * create service list
     *
     * @param isShowProgress
     */
    fun createService(name: String?, description: String?, isShowProgress: Boolean) {
        if (!name.isNullOrEmpty() && !description.isNullOrEmpty()) {
            if (isShowProgress) showProgress()
            callCommonApi({ obj ->
                NSServiceRepository.createService(
                    name,
                    description,
                    obj
                )
            }, { _, _ ->
                getServiceListApi(false) { serviceList, fleetList, _ ->

                }
            })
        }
    }


    /**
     * Get service list
     *
     * @param isShowProgress
     */
    fun getServiceListApi(isShowProgress: Boolean, callback: ((MutableList<NSGetServiceListData>, MutableList<FleetData>, MutableList<CapabilitiesDataItem>) -> Unit)) {
        if (isShowProgress) showProgress()

        callCommonApi({ obj ->
            NSServiceRepository.getServiceList(obj)
        }, { data, _ ->
            if (data is NSGetServiceListResponse) {
                data.data.sortByDescending { it.serviceId }
                getCapabilityList(data.data, callback)
            }
        })
    }

    private fun getCapabilityList(list: MutableList<NSGetServiceListData>, callback: ((MutableList<NSGetServiceListData>, MutableList<FleetData>, MutableList<CapabilitiesDataItem>) -> Unit)) {
        getCapabilitiesList(isCapabilityCheck = true) {
            getFleetList(list, it, callback)
        }
    }

    private fun getFleetList(list: MutableList<NSGetServiceListData>, capabilities: MutableList<CapabilitiesDataItem>,callback: ((MutableList<NSGetServiceListData>, MutableList<FleetData>, MutableList<CapabilitiesDataItem>) -> Unit)) {
        getFleetListApi { fleets ->
            callback.invoke(list, fleets, capabilities)
            hideProgress()
        }
    }

    fun getServiceCapability(serviceId: String, callback: ((ServiceCapabilitiesDataItem) -> Unit)) {
        callCommonApi({ obj ->
            NSCapabilitiesRepository.getServiceCapabilities(serviceId, obj)
        }, { data, isSuccess ->
            if (!isSuccess) {
                callback.invoke(ServiceCapabilitiesDataItem())
            } else if (data is NSServiceCapabilityResponse) {
                data.data?.let { callback.invoke(it) }
            }
        }, false)
    }

    /**
     * Service Enable disable
     *
     */
    fun serviceEnableDisable(serviceId: String, isEnable: Boolean) {
        callCommonApi({ obj ->
            NSServiceRepository.enableDisableService(serviceId, isEnable, obj)
        }, { _, _ ->

        })
    }

    /**
     * service capability update
     *
     */
    fun serviceCapabilityUpdate(serviceId: String, capabilityId: String, isFleetUpdate: Boolean, fleets: List<String>) {
        showProgress()
        callCommonApi({ obj ->
            NSCapabilitiesRepository.updateServiceCapability(serviceId, capabilityId, obj)
        }, { _, _ ->
            if (isFleetUpdate) {
                serviceFleetsUpdate(serviceId, fleets)
            } else {
                hideProgress()
            }
        })
    }

    /**
     * service capability update
     *
     */
    fun serviceFleetsUpdate(serviceId: String, fleets: List<String>) {
        showProgress()
        callCommonApi({ obj ->
            NSCapabilitiesRepository.updateServiceFleets(serviceId, fleets, obj)
        }, { _, _ ->
            hideProgress()
        })
    }

    override fun apiResponse(data: Any) {

    }
}
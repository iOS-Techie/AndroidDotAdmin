package com.nyotek.dot.admin.ui.serviceManagement

import android.app.Application
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.callbacks.NSCapabilityCallback
import com.nyotek.dot.admin.common.callbacks.NSFleetListCallback
import com.nyotek.dot.admin.common.callbacks.NSServiceCapabilityCallback
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.repository.NSCapabilitiesRepository
import com.nyotek.dot.admin.repository.NSServiceRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSCapabilitiesBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSCreateServiceResponse
import com.nyotek.dot.admin.repository.network.responses.NSFleetBlankDataResponse
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListResponse
import com.nyotek.dot.admin.repository.network.responses.NSServiceCapabilityResponse
import com.nyotek.dot.admin.repository.network.responses.ServiceCapabilitiesDataItem
import com.nyotek.dot.admin.ui.capabilities.NSCapabilitiesViewModel
import com.nyotek.dot.admin.ui.fleets.NSFleetViewModel

class NSServiceManagementViewModel(application: Application) : NSViewModel(application) {
    var isServiceListAvailable = NSSingleLiveEvent<Boolean>()
    var serviceItemList: MutableList<NSGetServiceListData> = arrayListOf()
    var capabilityItemList: MutableList<CapabilitiesDataItem> = arrayListOf()
    var fleetItemList: MutableList<FleetData> = arrayListOf()
    var createdServiceName: String? = null
    var createdServiceDescription: String? = null
    var selectedFilterList: MutableList<ActiveInActiveFilter> = arrayListOf()
    private var viewModel: NSCapabilitiesViewModel? = null
    var fleetViewModel: NSFleetViewModel? = null
    var selectedFleets: List<String> = arrayListOf()
    var selectedServiceId: String? = null
    var isFleetNeedToUpdate: Boolean = false

    fun setCapabilityModel(model: NSCapabilitiesViewModel) {
        viewModel = model
    }

    fun setFleetModel(model: NSFleetViewModel) {
        fleetViewModel = model
    }

    /**
     * create service list
     *
     * @param isShowProgress
     */
    fun createService(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (!createdServiceName.isNullOrEmpty() && !createdServiceDescription.isNullOrEmpty()) {
            NSServiceRepository.createService(createdServiceName!!, createdServiceDescription!!, this)
        } else {
            isProgressShowing.value = true
        }
    }


    /**
     * Get user detail
     *
     * @param isShowProgress
     */
    fun getServiceList(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSServiceRepository.getServiceList(this)
    }

    fun getServiceCapability(serviceId: String, callback: NSServiceCapabilityCallback) {
        NSCapabilitiesRepository.getServiceCapabilities(
            serviceId,
            object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    if (data is NSServiceCapabilityResponse) {
                        data.data?.let { callback.onDataItem(it) }
                    }
                }

                override fun onError(errors: List<Any>) {
                    callback.onDataItem(ServiceCapabilitiesDataItem())
                }

                override fun onFailure(failureMessage: String?) {
                    callback.onDataItem(ServiceCapabilitiesDataItem())
                }

                override fun <T> onNoNetwork(localData: T) {
                   callback.onDataItem(ServiceCapabilitiesDataItem())
                   handleNoNetwork()
                }
            })
    }

    /**
     * Service Enable disable
     *
     * @param isShowProgress
     */
    fun serviceEnableDisable(serviceId: String, isEnable: Boolean, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSServiceRepository.enableDisableService(serviceId, isEnable, this)
    }

    /**
     * service capability update
     *
     * @param isShowProgress
     */
    fun serviceCapabilityUpdate(serviceId: String, capabilityId: String, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSCapabilitiesRepository.updateServiceCapability(serviceId, capabilityId, this)
    }

    /**
     * service capability update
     *
     * @param isShowProgress
     */
    fun serviceFleetsUpdate(serviceId: String, fleets: List<String>, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSCapabilitiesRepository.updateServiceFleets(serviceId, fleets, this)
    }

    override fun apiResponse(data: Any) {
        when (data) {
            is NSGetServiceListResponse -> {
                serviceItemList.clear()
                if (data.data.isValidList()) {
                    serviceItemList.addAll(data.data)
                }
                serviceItemList.sortByDescending { it.serviceId }
                viewModel?.getCapabilitiesList(false, isCapabilityAvailableCheck = true, callback = object :
                    NSCapabilityCallback {
                    override fun onCapability(capabilities: MutableList<CapabilitiesDataItem>) {
                        capabilityItemList = capabilities
                        fleetViewModel?.getFleetList(false, object : NSFleetListCallback {
                            override fun onFleets(fleetList: MutableList<FleetData>) {
                                fleetItemList = fleetList
                                isServiceListAvailable.value = true
                            }
                        })
                    }
                })
            }
            is NSBlankDataResponse -> {
                isProgressShowing.value = false
            }
            is NSCapabilitiesBlankDataResponse -> {
                if (isFleetNeedToUpdate) {
                    serviceFleetsUpdate(selectedServiceId!!, selectedFleets, true)
                } else {
                    isProgressShowing.value = false
                }
            }
            is NSFleetBlankDataResponse -> {
                isProgressShowing.value = false
            }
            is NSCreateServiceResponse -> {
                getServiceList(false)
            }
        }
    }
}
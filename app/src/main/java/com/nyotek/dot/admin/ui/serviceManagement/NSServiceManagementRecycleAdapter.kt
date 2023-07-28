package com.nyotek.dot.admin.ui.serviceManagement

import android.app.Activity
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.callbacks.NSCapabilityListCallback
import com.nyotek.dot.admin.common.callbacks.NSItemSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSServiceCapabilityCallback
import com.nyotek.dot.admin.common.callbacks.NSServiceCapabilityUpdateCallback
import com.nyotek.dot.admin.common.callbacks.NSSwitchCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutCommonSpinnerBinding
import com.nyotek.dot.admin.databinding.LayoutServiceItemBinding
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.FleetServiceResponse
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.ServiceCapabilitiesDataItem


var fleetItemList: MutableList<FleetData> = arrayListOf()
var capabilityItemList: MutableList<CapabilitiesDataItem> = arrayListOf()
val tempCapabilityNameList: MutableList<String> = arrayListOf()
val tempCapabilityIdList: MutableList<String> = arrayListOf()

class NSServiceManagementRecycleAdapter(
    private val activity: Activity,
    private val viewModel: NSServiceManagementViewModel,
    private val callback: NSServiceCapabilityUpdateCallback,
    private val switchEnableDisableCallback: NSSwitchCallback
) : BaseViewBindingAdapter<LayoutServiceItemBinding, NSGetServiceListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutServiceItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource, _ ->
        with(binding) {
            response.apply {
                var selectedCapabilityId: String? = null
                var selectedFleets: MutableList<String> = arrayListOf()
                val capabilityItem = NSApplication.getInstance().getCapabilityItemList()[serviceId]
                stringResource.apply {
                    layoutFleets.tvCommonTitle.text = fleet
                    layoutFleets.clSelectAll.setVisibility(true)
                    switchService.switchEnableDisable(isActive)
                    tvItemActive.status(isActive)
                    tvViewMore.text = update
                    tvViewMore.setTextColor(ColorResources.getWhiteColor())
                    spinner.tvCommonTitle.text = capability.lowercase()
                    ColorResources.setBackgroundTint(clViewMore, ColorResources.getPrimaryColor())
                }

                tvItemTitle.text = response.name
                tvDate.text = NSDateTimeHelper.getDateForUser(response.created)

                clViewMore.setOnClickListener {
                    if (capabilityItem?.fleets != selectedFleets && selectedFleets.isEmpty()) {
                        viewModel.showError(stringResource.selectFleet)
                        return@setOnClickListener
                    }
                    callback.onItemSelect(response.serviceId!!, selectedCapabilityId!!, selectedFleets,
                        response.serviceId != selectedCapabilityId,  capabilityItem?.fleets != selectedFleets
                    )
                    capabilityItem?.fleets = selectedFleets
                }

                switchService.setOnClickListener {
                    isActive = !isActive
                    switchService.switchEnableDisable(isActive)
                    switchEnableDisableCallback.switch(serviceId!!, isActive)
                    tvItemActive.status(isActive)
                }

                fun setCapability(capabilityItem: ServiceCapabilitiesDataItem) {
                    setCapabilitySpinner(
                        activity,
                        capabilityItem,
                        spinner,
                        object : NSItemSelectCallback {
                            override fun onItemSelect(selectedId: String) {
                                selectedCapabilityId = selectedId
                            }
                        })

                    val list = fleetItemList.map { it.vendorId!! } as MutableList<String>
                    capabilityItem.fleets.sortBy { it }
                    list.sortBy { it }
                    layoutFleets.cbCheck.isChecked = capabilityItem.fleets == list

                    val fleetResponse: MutableList<FleetServiceResponse> = arrayListOf()
                    for (fResponse in fleetItemList) {
                        val fleetServiceResponse = FleetServiceResponse(fResponse, capabilityItem.fleets.contains(fResponse.vendorId))
                        fleetResponse.add(fleetServiceResponse)
                    }

                    NSUtilities.setFleet(
                        activity,
                        layoutFleets,
                        fleetResponse,
                        object :
                            NSCapabilityListCallback {
                            override fun onCapability(capabilities: MutableList<String>) {
                                selectedFleets = capabilities
                            }
                        })
                }

                viewModel.apply {
                    if (capabilityItem == null) {
                        getServiceCapability(serviceId!!, object : NSServiceCapabilityCallback {
                            override fun onDataItem(item: ServiceCapabilitiesDataItem) {
                                val map: HashMap<String, ServiceCapabilitiesDataItem> =
                                    NSApplication.getInstance().getCapabilityItemList()
                                map[serviceId] = item
                                NSApplication.getInstance().setCapabilityItemList(map)
                                setCapability(item)
                            }
                        })
                    } else {
                        setCapability(capabilityItem)
                    }
                }
            }
        }
    }
) {
    fun setSubList(capabilityList: MutableList<CapabilitiesDataItem>, fleetList: MutableList<FleetData>) {
        fleetItemList = fleetList
        capabilityItemList = capabilityList
        tempCapabilityNameList.clear()
        tempCapabilityIdList.clear()
        tempCapabilityNameList.add(stringResource.selectCapability)
        tempCapabilityIdList.add("")
        for (data in capabilityItemList) {
            tempCapabilityNameList.add(getLngValue(data.label))
            tempCapabilityIdList.add(data.id?:"")
        }
    }
}

fun setCapabilitySpinner(activity: Activity, item: ServiceCapabilitiesDataItem?, spinner: LayoutCommonSpinnerBinding, itemCallback: NSItemSelectCallback) {
    var selectedCapabilityId: String? = null
    var spinnerPosition = -1
    if (item != null) {
        spinnerPosition = tempCapabilityIdList.indexOf(item.capabilityId)
    }
    NSUtilities.setSpinner(
        activity,
        spinner.spinnerAppSelect,
        tempCapabilityNameList,
        tempCapabilityIdList,
        object :
            NSItemSelectCallback {
            override fun onItemSelect(selectedId: String) {
                if (selectedCapabilityId != selectedId) {
                    selectedCapabilityId = selectedId
                    itemCallback.onItemSelect(selectedId)
                }
            }
        },
        true
    )
    if (spinnerPosition != -1) {
        spinner.spinnerAppSelect.setSelection(spinnerPosition)
    }
}
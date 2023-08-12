package com.nyotek.dot.admin.ui.serviceManagement

import android.app.Activity
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.setPlaceholderAdapter
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
import com.nyotek.dot.admin.repository.network.responses.SpinnerData
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse


var fleetItemList: MutableList<FleetData> = arrayListOf()
var capabilityItemList: MutableList<CapabilitiesDataItem> = arrayListOf()

class NSServiceManagementRecycleAdapter(
    private val activity: Activity,
    private val viewModel: NSServiceManagementViewModel,
    private val selectedCapabilityCallback: (String, String) -> Unit,
    private val selectedFleetCallback: (String, List<String>) -> Unit,
    private val switchCallback: (String, Boolean) -> Unit
) : BaseViewBindingAdapter<LayoutServiceItemBinding, NSGetServiceListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutServiceItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource, _ ->
        with(binding) {
            response.apply {
                val capabilityItem = NSApplication.getInstance().getCapabilityItemList()[serviceId]
                stringResource.apply {
                    layoutFleets.tvCommonTitle.text = fleet
                    layoutFleets.clSelectAll.setVisibility(true)
                    switchService.switchEnableDisable(isActive)
                    tvItemActive.status(isActive)
                    tvViewMore.text = update
                    tvDescription.text = description
                    tvViewMore.setTextColor(ColorResources.getWhiteColor())
                    spinner.tvCommonTitle.text = capability.lowercase()
                    ColorResources.setBackgroundTint(clViewMore, ColorResources.getPrimaryColor())
                }

                tvItemTitle.text = response.name
                tvDate.text = NSDateTimeHelper.getDateForUser(response.created)

                switchService.setOnClickListener {
                    isActive = !isActive
                    switchService.switchEnableDisable(isActive)
                    switchCallback.invoke(serviceId!!, isActive)
                    tvItemActive.status(isActive)
                }

                fun setCapabilityAndFleet(capabilityItem: ServiceCapabilitiesDataItem) {
                    //Set Capability in Spinner
                    setCapabilitySpinner(
                        activity,
                        stringResource,
                        capabilityItem,
                        spinner) {
                        capabilityItem.capabilityId = it
                        selectedCapabilityCallback.invoke(response.serviceId!!, it)
                    }

                    //Compare Select All for Main FleetList with capability fleet list
                    val list = fleetItemList.map { it.vendorId!! } as MutableList<String>
                    capabilityItem.fleets.sortBy { it }
                    list.sortBy { it }
                    layoutFleets.cbCheck.isChecked = capabilityItem.fleets == list

                    //Add Fleet Selected or not
                    val fleetResponse: MutableList<FleetServiceResponse> = arrayListOf()
                    for (fResponse in fleetItemList) {
                        val fleetServiceResponse = FleetServiceResponse(fResponse, capabilityItem.fleets.contains(fResponse.vendorId))
                        fleetResponse.add(fleetServiceResponse)
                    }

                    //Set Fleet in list
                    NSUtilities.setFleet(
                        activity,
                        layoutFleets,
                        fleetResponse) {
                        selectedFleetCallback.invoke(response.serviceId!!, it)
                        capabilityItem.fleets = it
                    }
                }

                viewModel.apply {
                    if (capabilityItem == null) {
                        //Api calling of Capability List
                        getServiceCapability(serviceId!!) {
                            val map: HashMap<String, ServiceCapabilitiesDataItem> =
                                NSApplication.getInstance().getCapabilityItemList()
                            map[serviceId] = it
                            NSApplication.getInstance().setCapabilityItemList(map)
                            setCapabilityAndFleet(it)
                        }
                    } else {
                        setCapabilityAndFleet(capabilityItem)
                    }
                }
            }
        }
    }
) {
    fun setSubList(capabilityList: MutableList<CapabilitiesDataItem>, fleetList: MutableList<FleetData>) {
        fleetItemList = fleetList
        capabilityItemList = capabilityList
    }
}

fun setCapabilitySpinner(activity: Activity, stringResource: StringResourceResponse, item: ServiceCapabilitiesDataItem?, spinner: LayoutCommonSpinnerBinding, itemCallback: ((String) -> Unit)) {
    val nameList: MutableList<String> = capabilityItemList.map { getLngValue(it.label) }.toMutableList()
    val idList: MutableList<String> = capabilityItemList.map { it.id ?: "" }.toMutableList()
    val spinnerList = SpinnerData(idList, nameList)

    spinner.spinnerAppSelect.setPlaceholderAdapter(spinnerList, activity, item?.capabilityId, isHideFirstPosition = true, placeholderName = stringResource.selectCapability) { selectedId ->
        if (selectedId != item?.capabilityId && selectedId?.isNotEmpty() == true) {
            item?.capabilityId = selectedId
            itemCallback.invoke(selectedId)
        }
    }
}
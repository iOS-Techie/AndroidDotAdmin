package com.nyotek.dot.admin.ui.tabs.services

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutServiceItemBinding
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.NSGetServiceListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class NSServiceManagementRecycleAdapter @Inject constructor(
    private val themeUI: ServiceUI,
    private val allCapabilitiesList: MutableList<CapabilitiesDataItem>?,
    private val fleetDataList: MutableList<FleetData>?,
    private val selectedCapabilityCallback: (String, String) -> Unit,
    private val selectedFleetCallback: (String, List<String>) -> Unit,
    private val switchCallback: (String, Boolean) -> Unit
) : BaseViewBindingAdapter<LayoutServiceItemBinding, NSGetServiceListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutServiceItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, _, _ ->
        with(binding) {
            response.apply {
                themeUI.setServiceAdapterUI(binding, isActive)
                themeUI.fetchServiceCapabilities(binding, serviceId, allCapabilitiesList, {
                    selectedCapabilityCallback.invoke(response.serviceId!!, it)
                }) {
                    themeUI.setFleets(binding, it, fleetDataList) { list ->
                        selectedFleetCallback.invoke(response.serviceId?:"", list)
                    }
                }

                tvItemTitle.text = response.name
                tvDescription.text = description

                CoroutineScope(Dispatchers.Main).launch {
                    val formattedDate = NSDateTimeHelper.getDateForUser(response.created)
                    tvDate.text = formattedDate
                }

                switchService.setOnClickListener {
                    isActive = !isActive
                    switchService.switchEnableDisable(isActive)
                    switchCallback.invoke(serviceId!!, isActive)
                    tvItemActive.status(isActive)
                }
            }
        }
    }
)
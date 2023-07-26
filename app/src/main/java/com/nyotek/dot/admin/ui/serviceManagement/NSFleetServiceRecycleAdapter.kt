package com.nyotek.dot.admin.ui.serviceManagement

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.callbacks.NSFleetServiceCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesVehicleBinding
import com.nyotek.dot.admin.repository.network.responses.FleetData

private var selectedList: MutableList<String> = arrayListOf()

class NSFleetServiceRecycleAdapter(
    private val callback: NSFleetServiceCallback
) : BaseViewBindingAdapter<LayoutCapabilitiesVehicleBinding, FleetData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutCapabilitiesVehicleBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _ ->
        with(binding) {
            response.apply {
                ColorResources.setCardBackground(viewStatus, 100f, 0, if (response.isActive) ColorResources.getGreenColor() else ColorResources.getGrayColor())
                tvCapabilitiesTitle.getMapValue(response.name)

                if (selectedList.contains(response.vendorId)) {
                    cbCapability.isChecked = true
                    callback.onItemSelect(response, !cbCapability.isChecked)
                } else {
                    cbCapability.isChecked = false
                }

                clCapabilities.setOnClickListener {
                    cbCapability.isChecked = !cbCapability.isChecked
                    callback.onItemSelect(response, !cbCapability.isChecked)
                }
            }
        }
    }
) {
    fun setSubList(capabilityList: MutableList<String>) {
        selectedList = capabilityList
    }
}
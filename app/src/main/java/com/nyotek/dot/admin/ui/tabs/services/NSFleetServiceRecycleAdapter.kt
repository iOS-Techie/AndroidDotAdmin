package com.nyotek.dot.admin.ui.tabs.services

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesVehicleBinding
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.FleetServiceResponse
import javax.inject.Inject

class NSFleetServiceRecycleAdapter  @Inject constructor(
    private val colorResources: ColorResources,
    private val callback: ((FleetData, Boolean) -> Unit?)
) : BaseViewBindingAdapter<LayoutCapabilitiesVehicleBinding, FleetServiceResponse>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutCapabilitiesVehicleBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, _, _ ->
        with(binding) {
            response.apply {
                colorResources.setCardBackground(viewStatus, 100f, 0, if (response.data?.isActive == true) colorResources.getGreenColor() else colorResources.getGrayColor())
                response.data?.name?.let { tvCapabilitiesTitle.getMapValue(it) }

                cbCapability.isChecked = response.isSelected

                clCapabilities.setOnClickListener {
                    cbCapability.isChecked = !cbCapability.isChecked
                    callback.invoke(response.data!!, !cbCapability.isChecked)
                }
            }
        }
    }
)
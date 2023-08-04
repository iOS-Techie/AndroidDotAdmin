package com.nyotek.dot.admin.ui.fleets.vehicle

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.glideCenter
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutVehicleListItemBinding
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem

class NSVehicleRecycleAdapter(
    private val editVehicleCallback: ((VehicleDataItem, Int) -> Unit),
    private val switchCallBack: ((String, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutVehicleListItemBinding, VehicleDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutVehicleListItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position ->
        with(binding) {
            response.apply {
                binding.ivVehicleImg.glideCenter(url = vehicleImg)

                tvVehicleTitle.text = manufacturer
                val capability = "- $capabilityNameList"
                tvDescription.text = capability
                tvManufacturer.text = model
                tvDescription.setVisibility(capabilityNameList?.isNotEmpty() == true)
                switchService.switchEnableDisable(isActive)

                switchService.setSafeOnClickListener {
                    isActive = !isActive
                    switchService.switchEnableDisable(isActive)
                    switchCallBack.invoke(id!!, isActive)
                }

                ivEdit.setSafeOnClickListener {
                    editVehicleCallback.invoke(this, position)
                }
            }
        }
    }
)
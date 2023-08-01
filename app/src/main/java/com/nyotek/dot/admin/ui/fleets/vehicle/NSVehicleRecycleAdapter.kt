package com.nyotek.dot.admin.ui.fleets.vehicle

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.glide
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
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
                binding.ivVehicleImg.glide(url = vehicleImg)

                tvVehicleTitle.text = manufacturer
                val year = "- $manufacturingYear"
                tvDescription.text = year
                tvManufacturer.text = model

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
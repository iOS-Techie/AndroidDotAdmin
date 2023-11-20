package com.nyotek.dot.admin.ui.fleets.vehicle

import android.app.Activity
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.glideCenter
import com.nyotek.dot.admin.common.utils.setGlideWithPlaceHolder
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setVisibilityIn
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutVehicleListItemBinding
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem

class NSVehicleRecycleAdapter(
    private val activity: Activity,
    private val editVehicleCallback: ((VehicleDataItem, Int) -> Unit),
    private val switchCallBack: ((String, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutVehicleListItemBinding, VehicleDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutVehicleListItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position ->
        with(binding) {
            response.apply {
                binding.ivVehicleImg.setGlideWithPlaceHolder(activity, url = vehicleImg)

                tvVehicleTitle.text = manufacturer
                val capability = "- $capabilityNameList"
                tvDescription.text = capability
                tvManufacturer.text = model
                tvDescription.setVisibilityIn(capabilityNameList?.isNotEmpty() == true)
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
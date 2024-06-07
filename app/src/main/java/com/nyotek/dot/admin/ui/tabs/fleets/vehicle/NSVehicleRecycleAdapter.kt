package com.nyotek.dot.admin.ui.tabs.fleets.vehicle

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.extension.setCoil
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutVehicleListItemBinding
import com.nyotek.dot.admin.models.responses.VehicleDataItem

class NSVehicleRecycleAdapter(
    private val themeUI: VehicleUI,
    private val editVehicleCallback: ((VehicleDataItem, Int) -> Unit),
    private val switchCallBack: ((String, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutVehicleListItemBinding, VehicleDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutVehicleListItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, _ ->
        with(binding) {
            themeUI.setAdapter(binding)
            response.apply {
                layoutVehicle.apply {
                    ivIcon.setCoil(url = vehicleImg, NSConstants.FILL, 0f)
                    layoutName.tvDetail.text = manufacturer
                    layoutNumber.tvDetail.text = model
                    layoutEmail.tvDetail.text = registrationNo
                }
                layoutVehicleSecond.apply {
                    layoutName.tvDetail.text = manufacturingYear
                    layoutNumber.tvDetail.text = loadCapacity
                    layoutEmail.tvDetail.text = capabilityNameList
                }

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
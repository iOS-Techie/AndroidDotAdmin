package com.nyotek.dot.admin.ui.fleets.vehicle

import android.app.Activity
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.glideCenter
import com.nyotek.dot.admin.common.utils.glideWithPlaceHolder
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.invisible
import com.nyotek.dot.admin.common.utils.rotation
import com.nyotek.dot.admin.common.utils.setGlideWithPlaceHolder
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.common.utils.setVisibilityIn
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.common.utils.visible
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

    onBind = { binding, response, stringResource, position, _ ->
        with(binding) {
            response.apply {
                layoutVehicle.apply {
                    stringResource.apply {
                        rlName.visible()
                        rlNumber.visible()
                        rlEmail.visible()
                        layoutName.tvItemTitle.text = manufacturer
                        layoutNumber.tvItemTitle.text = model
                        layoutEmail.tvItemTitle.text = registrationNo
                        rlAddress.gone()
                        viewLine.invisible()
                    }
                }

                layoutVehicleSecond.apply {
                    stringResource.apply {
                        cardImg.invisible()
                        rlName.visible()
                        rlNumber.visible()
                        rlEmail.visible()
                        layoutName.tvItemTitle.text = stringResource.manufacturerYear
                        layoutNumber.tvItemTitle.text = loadCapacity
                        layoutEmail.tvItemTitle.text = capabilities
                        rlAddress.gone()
                    }
                }

                switchService.rotation()
                layoutVehicle.apply {
                    ivIcon.glideWithPlaceHolder(url = vehicleImg)
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
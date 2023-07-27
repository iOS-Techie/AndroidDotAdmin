package com.nyotek.dot.admin.ui.fleets

import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.callbacks.NSFleetDetailCallback
import com.nyotek.dot.admin.common.callbacks.NSSwitchEnableDisableCallback
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.glide200
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutFleetItemBinding
import com.nyotek.dot.admin.repository.network.responses.FleetData

class NSFleetManagementRecycleAdapter(
    private val fleetDetailCallback: NSFleetDetailCallback,
    private val switchEnableDisableCallback: NSSwitchEnableDisableCallback
) : BaseViewBindingAdapter<LayoutFleetItemBinding, FleetData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutFleetItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource, _ ->
        with(binding) {
            response.apply {
                tvViewMore.text = stringResource.view
                tvItemActive.status(isActive)
                tvItemTitle.getMapValue(name)
                ivVendor.glide200(R.drawable.ic_place_holder_img, url = response.logo, scale = logoScale)

                switchFleet.switchEnableDisable(isActive)

                clViewMoreVendor.setOnClickListener {
                    fleetDetailCallback.onItemSelect(response)
                }

                switchFleet.setOnClickListener {
                    isActive = !isActive
                    switchFleet.switchEnableDisable(isActive)
                    switchEnableDisableCallback.switch(vendorId!!, isActive)
                    tvItemActive.status(isActive)
                }
            }
        }
    }
)
package com.nyotek.dot.admin.ui.fleets

import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.glide200
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutFleetItemBinding
import com.nyotek.dot.admin.repository.network.responses.FleetData

class NSFleetManagementRecycleAdapter(
    private val fleetDetailCallback: ((FleetData) -> Unit),
    private val switchCallBack: ((String, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutFleetItemBinding, FleetData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutFleetItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource, _, _ ->
        with(binding) {
            response.apply {
                tvViewMore.text = stringResource.view
                tvItemActive.status(isActive)
                tvItemTitle.getMapValue(name)
                ivVendor.glide200(R.drawable.ic_place_holder_img, url = response.logo, scale = logoScale)

                switchFleet.switchEnableDisable(isActive)

                clViewMoreVendor.setOnClickListener {
                    fleetDetailCallback.invoke(response)
                }

                switchFleet.setOnClickListener {
                    isActive = !isActive
                    switchFleet.switchEnableDisable(isActive)
                    switchCallBack.invoke(vendorId!!, isActive)
                    tvItemActive.status(isActive)
                }
            }
        }
    }
)
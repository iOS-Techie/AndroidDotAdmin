package com.nyotek.dot.admin.ui.tabs.fleets

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.setCoil
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutFleetItemBinding
import com.nyotek.dot.admin.models.responses.FleetData

class NSFleetManagementRecycleAdapter(
    private val languageConfig: NSLanguageConfig,
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
                ivVendor.setCoil(response.logo, logoScale, 4f)
                switchFleet.rotation(languageConfig.isLanguageRtl())
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
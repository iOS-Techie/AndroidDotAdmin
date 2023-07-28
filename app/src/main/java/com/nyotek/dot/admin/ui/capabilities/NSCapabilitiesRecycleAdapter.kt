package com.nyotek.dot.admin.ui.capabilities

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.callbacks.NSCapabilitiesCallback
import com.nyotek.dot.admin.common.callbacks.NSSwitchCallback
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesBinding
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem

class NSCapabilitiesRecycleAdapter(
    private val callback: NSCapabilitiesCallback,
    private val switchEnableDisableCallback: NSSwitchCallback
) : BaseViewBindingAdapter<LayoutCapabilitiesBinding, CapabilitiesDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutCapabilitiesBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _,_ ->
        binding.apply {
            response.apply {

                tvActiveTitle.status(isActive)
                switchService.switchEnableDisable(isActive)
                tvCapabilitiesTitle.getMapValue(label)

                switchService.setOnClickListener {
                    isActive = !isActive
                    switchService.switchEnableDisable(isActive)
                    switchEnableDisableCallback.switch(id!!, isActive)
                    tvActiveTitle.status(isActive)
                }

                ivDelete.setSafeOnClickListener {
                    callback.onItemSelect(response, true)
                }

                ivEdit.setSafeOnClickListener {
                    callback.onItemSelect(response, false)
                }
            }
        }
    }
)
package com.nyotek.dot.admin.ui.capabilities

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.callbacks.NSSwitchCallback
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesBinding
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem

class NSCapabilitiesRecycleAdapter(
    private val callback: ((CapabilitiesDataItem, Boolean) -> Unit),
    private val switchCallBack: ((String, Boolean) -> Unit),
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
                    switchCallBack.invoke(id!!, isActive)
                    tvActiveTitle.status(isActive)
                }

                ivDelete.setSafeOnClickListener {
                    callback.invoke(response, true)
                }

                ivEdit.setSafeOnClickListener {
                    callback.invoke(response, false)
                }
            }
        }
    }
)
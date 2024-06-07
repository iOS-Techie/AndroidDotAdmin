package com.nyotek.dot.admin.ui.tabs.capabilities

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesBinding
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem
import javax.inject.Inject

class NSCapabilitiesRecycleAdapter @Inject constructor(
    private val themeUI: CapabilitiesUI,
    private val callback: ((CapabilitiesDataItem, Boolean) -> Unit),
    private val switchCallBack: ((String, Boolean) -> Unit),
) : BaseViewBindingAdapter<LayoutCapabilitiesBinding, CapabilitiesDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutCapabilitiesBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _,_, _ ->
        binding.apply {
            response.apply {
                themeUI.setCapabilityAdapterUI(binding)
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

                tvModify.setSafeOnClickListener {
                    callback.invoke(response, false)
                }
            }
        }
    }
)
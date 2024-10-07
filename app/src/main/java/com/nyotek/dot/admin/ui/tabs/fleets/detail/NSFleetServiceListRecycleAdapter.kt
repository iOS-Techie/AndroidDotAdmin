package com.nyotek.dot.admin.ui.tabs.fleets.detail

import android.app.Activity
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.extension.setAlphaP6
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutServiceListHorizontalBinding
import com.nyotek.dot.admin.models.responses.NSGetServiceListData

class NSFleetServiceListRecycleAdapter(
    val activity: Activity, var colorResources: ColorResources, var callback: ((String, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutServiceListHorizontalBinding, NSGetServiceListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutServiceListHorizontalBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, size ->
        with(binding) {
            response.apply {
                cbCheck.isChecked = response.isSelected
                tvTitle.text = response.name
                colorResources.setBackground(viewDivider, colorResources.getBackgroundColor())
                colorResources.setCardBackground(viewActive, 100f, 0, if (response.isActive) colorResources.getGreenColor() else colorResources.getPrimaryColor())

                viewActive.setAlphaP6(isActive)
                tvTitle.setAlphaP6(isActive)
                viewDivider.setVisibility(position != size - 1)
                
                clCheck.setOnClickListener {
                    cbCheck.isChecked = !cbCheck.isChecked
                    response.isSelected = cbCheck.isChecked
                    callback.invoke(serviceId ?: "", cbCheck.isChecked)
                }
            }
        }
    }
)
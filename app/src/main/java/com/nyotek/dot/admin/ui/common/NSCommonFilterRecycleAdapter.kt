package com.nyotek.dot.admin.ui.common

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutFilterVendorBinding
import com.nyotek.dot.admin.models.responses.ActiveInActiveFilter

private var list: MutableList<ActiveInActiveFilter> = arrayListOf()

class NSCommonFilterRecycleAdapter(
    private val colorResources: ColorResources,
    private val vendorFilterCallback: ((ActiveInActiveFilter, MutableList<ActiveInActiveFilter>) -> Unit?)
) : BaseViewBindingAdapter<LayoutFilterVendorBinding, ActiveInActiveFilter>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutFilterVendorBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, _, _ ->
        with(binding) {
            tvVendorTitle.text = response.title
            colorResources.setCardBackground(tvVendorTitle, 100f, 0, if (response.isActive) colorResources.getPrimaryColor() else colorResources.getBorderColor())
            tvVendorTitle.setTextColor(colorResources.getWhitePrimary(response.isActive))

            tvVendorTitle.setOnClickListener {
                for (data in list) {
                    data.isActive = data.title.equals(response.title)
                }
                vendorFilterCallback.invoke(response, list)
            }
        }
    }

) {
    fun setList() {
        list = getData()
    }
}
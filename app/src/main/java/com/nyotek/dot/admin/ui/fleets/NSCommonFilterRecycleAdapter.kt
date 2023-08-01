package com.nyotek.dot.admin.ui.fleets

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutFilterVendorBinding
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter

private var list: MutableList<ActiveInActiveFilter> = arrayListOf()

class NSCommonFilterRecycleAdapter(
    private val vendorFilterCallback: ((ActiveInActiveFilter, MutableList<ActiveInActiveFilter>) -> Unit?)
) : BaseViewBindingAdapter<LayoutFilterVendorBinding, ActiveInActiveFilter>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutFilterVendorBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, _ ->
        with(binding) {
            tvVendorTitle.text = response.title
            ColorResources.setCardBackground(tvVendorTitle, 100f, 0, if (response.isActive) ColorResources.getPrimaryColor() else ColorResources.getBorderColor())
            tvVendorTitle.setTextColor(if (response.isActive) ColorResources.getWhiteColor() else ColorResources.getPrimaryColor())

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
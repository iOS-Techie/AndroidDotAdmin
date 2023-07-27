package com.nyotek.dot.admin.ui.dashboard

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.callbacks.NSSideNavigationSelectCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.databinding.LayoutSideNavItemBinding
import com.nyotek.dot.admin.repository.network.responses.NSNavigationResponse


class NSSideNavigationRecycleAdapter(
    private val isLanguageSelected: Boolean,
    private val sideNavigationCallback: NSSideNavigationSelectCallback
) : BaseViewBindingAdapter<LayoutSideNavItemBinding, NSNavigationResponse>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutSideNavItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position ->
        with(binding) {
            response.apply {
                tvNavSubTitle.text = title
                val isSelected = NSApplication.getInstance().getSelectedNavigationType() == type
                val color = if (isSelected) ColorResources.getWhiteColor() else ColorResources.getPrimaryColor()

                ivIconNav.setImageResource(icon)
                tvNavSubTitle.setTextColor(color)
                viewLineNav.setVisibility(!isSelected)
                ivIconNav.setColorFilter(if (isSelected) ColorResources.getWhiteColor() else ColorResources.getPrimaryColor())

                if (isSelected) {
                    ColorResources.setBackground(clItem, ColorResources.getPrimaryColor())
                } else {
                    ivIconNav.setColorFilter(ColorResources.getPrimaryColor())
                    clItem.setBackgroundResource(0)
                }

                ivSideArrow.setVisibility(isSelected)
                ivSideArrow.rotation = if (isLanguageSelected) 180f else 0f

                clItem.setOnClickListener {
                    sideNavigationCallback.onItemSelect(response, position)
                }
            }
        }
    }
)
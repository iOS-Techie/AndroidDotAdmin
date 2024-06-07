package com.nyotek.dot.admin.ui.main

import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.databinding.LayoutSideNavItemBinding
import com.nyotek.dot.admin.models.responses.NSNavigationResponse
import javax.inject.Inject

var selectedDrawerId: Int = R.id.dashboard

class NSSideNavigationRecycleAdapter @Inject constructor(
    private val isLanguageSelected: Boolean,
    private val themeUI: MainUI,
    private val sideNavigationCallback: (NSNavigationResponse, Int) -> Unit
) : BaseViewBindingAdapter<LayoutSideNavItemBinding, NSNavigationResponse>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutSideNavItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, _ ->
        with(binding) {
            response.apply {
                val isSelected = response.id == selectedDrawerId
                themeUI.setSideNavUI(binding, isSelected)

                tvNavSubTitle.text = title
                ivIconNav.setImageResource(icon)

                clItem.setOnClickListener {
                    sideNavigationCallback.invoke(response, position)
                }
            }
        }
    }
)
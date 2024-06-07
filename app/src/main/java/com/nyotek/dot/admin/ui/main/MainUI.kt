package com.nyotek.dot.admin.ui.main

import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.FragmentMainBinding
import com.nyotek.dot.admin.databinding.LayoutSideNavItemBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainUI @Inject constructor(private val binding: FragmentMainBinding, private val colorResources: ColorResources) {

    init {
        binding.apply {
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {
                    tvUserTitle.text = logout
                }
            }
        }
    }

    fun setSideNavUI(bind: LayoutSideNavItemBinding, isSelected: Boolean) {
        bind.apply {
            colorResources.apply {
                val color = getPrimaryGray(isSelected)
                tvNavSubTitle.setTextColor(color)
                ivIconNav.setColorFilter(color)

                ivSideArrow.setVisibility(isSelected)
                viewLineSide.setVisibility(isSelected)
            }
        }
    }
}
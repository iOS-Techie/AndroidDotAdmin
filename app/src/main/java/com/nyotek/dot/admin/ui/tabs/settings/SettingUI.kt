package com.nyotek.dot.admin.ui.tabs.settings

import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.NsFragmentSettingsBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingUI @Inject constructor(private val binding: NsFragmentSettingsBinding, private val colorResources: ColorResources) {

    init {
        binding.apply {
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {
                    colorResources.setCardBackground(rvSettings, 14f, 1, colorResources.getWhiteColor(), colorResources.getBorderColor())
                }
            }
        }
    }
}
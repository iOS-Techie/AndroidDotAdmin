package com.nyotek.dot.admin.ui.tabs.fleets.employee

import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutEmployeeListBinding
import com.nyotek.dot.admin.databinding.NsFragmentEmployeeBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeUI @Inject constructor(private val binding: NsFragmentEmployeeBinding, private val colorResources: ColorResources, private val languageConfig: NSLanguageConfig) {

    init {
        binding.apply {
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {

            }
        }
    }

    fun setAdapter(bind: LayoutEmployeeListBinding, isEmployeeSelected: Boolean) {
        bind.apply {
            switchService.rotation(languageConfig.isLanguageRtl())
            colorResources.setBackground(clEmployeeItem, if (isEmployeeSelected) colorResources.getBackgroundColor() else colorResources.getWhiteColor())

        }
    }
}
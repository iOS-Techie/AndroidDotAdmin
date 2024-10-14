package com.nyotek.dot.admin.ui.tabs.fleets.employee.detail

import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.status
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutEmployeeListBinding
import com.nyotek.dot.admin.databinding.NsFragmentDriverDetailBinding
import com.nyotek.dot.admin.databinding.NsFragmentEmployeeBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriverDetailUI @Inject constructor(private val binding: NsFragmentDriverDetailBinding, private val colorResources: ColorResources, private val languageConfig: NSLanguageConfig) {

    init {
        binding.apply {
            colorResources.setCardBackground(clDriverItem, 10f, 1, colorResources.getWhiteColor(),colorResources.getBorderColor())
            setTextViews()
        }
    }

    private fun setTextViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {
                    switchService.rotation(languageConfig.isLanguageRtl())
                    layoutHomeHeader.tvHeaderTitle.text = driverDetail
                    tvVehicleTitle.text = driverDetail
                    layoutName.tvCommonTitle.text = name
                    layoutFleet.tvCommonTitle.text = fleet
                    spinner.tvCommonTitle.text = updateVehicle
                    spinnerRole.tvCommonTitle.text = employeeRole
                    clDriverItem.gone()
                    layoutFleet.edtValue.isEnabled = false
                }
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
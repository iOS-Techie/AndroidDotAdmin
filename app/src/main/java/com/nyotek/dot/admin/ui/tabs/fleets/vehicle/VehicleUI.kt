package com.nyotek.dot.admin.ui.tabs.fleets.vehicle

import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.invisible
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutEmployeeListBinding
import com.nyotek.dot.admin.databinding.LayoutVehicleListItemBinding
import com.nyotek.dot.admin.databinding.NsFragmentEmployeeBinding
import com.nyotek.dot.admin.databinding.NsFragmentFleetsBinding
import com.nyotek.dot.admin.databinding.NsFragmentVehicleBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleUI @Inject constructor(private val binding: NsFragmentVehicleBinding, private val colorResources: ColorResources, private val languageConfig: NSLanguageConfig) {

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

    fun setAdapter(bind: LayoutVehicleListItemBinding) {
        bind.apply {
            colorResources.apply {
                layoutVehicle.apply {
                    getStringResource().apply {
                        rlName.visible()
                        rlNumber.visible()
                        rlEmail.visible()
                        layoutName.tvItemTitle.text = manufacturer
                        layoutNumber.tvItemTitle.text = model
                        layoutEmail.tvItemTitle.text = registrationNo
                        rlAddress.gone()
                        viewLine.invisible()
                    }
                }

                layoutVehicleSecond.apply {
                    getStringResource().apply {
                        cardImg.invisible()
                        rlName.visible()
                        rlNumber.visible()
                        rlEmail.visible()
                        layoutName.tvItemTitle.text = getStringResource().manufacturerYear
                        layoutNumber.tvItemTitle.text = loadCapacity
                        layoutEmail.tvItemTitle.text = capabilities
                        rlAddress.gone()
                    }
                }

                switchService.rotation(languageConfig.isLanguageRtl())

            }
        }
    }
}
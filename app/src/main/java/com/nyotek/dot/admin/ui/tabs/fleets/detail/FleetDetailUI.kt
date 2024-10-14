package com.nyotek.dot.admin.ui.tabs.fleets.detail

import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.NsFragmentFleetDetailBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FleetDetailUI @Inject constructor(private val binding: NsFragmentFleetDetailBinding, private val colorResources: ColorResources) {

    init {
        binding.apply {
            setUpViews()
        }
    }

    private fun setUpViews() {
        binding.apply {
            colorResources.apply {
                getStringResource().apply {
                    tvCreateFleetTitle.text = fleets
                    tvSave.text = save
                    tvCancel.text = cancel

                }
            }
        }
    }
}
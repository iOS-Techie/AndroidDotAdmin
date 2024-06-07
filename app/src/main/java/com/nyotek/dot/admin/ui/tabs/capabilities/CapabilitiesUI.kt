package com.nyotek.dot.admin.ui.tabs.capabilities

import android.view.View
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.extension.rotation
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesBinding
import com.nyotek.dot.admin.databinding.LayoutCreateCapabiltiesBinding
import com.nyotek.dot.admin.databinding.NsFragmentCapabilitiesBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CapabilitiesUI @Inject constructor(private val binding: NsFragmentCapabilitiesBinding, private val colorResources: ColorResources, private val languageConfig: NSLanguageConfig) {

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

    fun setCapabilityAdapterUI(bind: LayoutCapabilitiesBinding) {
        bind.apply {
            colorResources.apply {
                switchService.rotation(languageConfig.isLanguageRtl())
                tvModify.text = getStringResource().modify
            }
        }
    }

    fun setCapabilitiesCreateEditUI(bind: LayoutCreateCapabiltiesBinding, isCreate: Boolean) {
        bind.apply {
            colorResources.getStringResource().apply {
                tvCapabilityTitle.text = if (isCreate) createCapabilities else updateCapabilities
                viewLine.setVisibility(isCreate)
                tvCapabilityActive.text = inActive
                layoutName.tvCommonTitle.text = name
                tvCancel.text = cancel
                tvSave.text = if (isCreate) create else update
                layoutName.rvLanguageTitle.visibility = View.VISIBLE
                layoutName.edtValue.setText("")
            }
        }
    }
}
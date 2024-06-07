package com.nyotek.dot.admin.ui.tabs.dispatch

import android.view.View
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesBinding
import com.nyotek.dot.admin.databinding.LayoutCreateCapabiltiesBinding
import com.nyotek.dot.admin.databinding.LayoutDispatchListBinding
import com.nyotek.dot.admin.databinding.NsFragmentCapabilitiesBinding
import com.nyotek.dot.admin.databinding.NsFragmentDispatchBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DispatchUI @Inject constructor(private val binding: NsFragmentDispatchBinding, private val colorResources: ColorResources) {

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

    fun setDispatchAdapterUI(bind: LayoutDispatchListBinding, status: String?, assignedDriverId: String?) {
        bind.apply {
            colorResources.apply {
                setCardBackground(clDispatchDetailView, 8f, width = 1)
                setBackground(viewLineDivider, getSecondaryDarkColor())
                setBackground(viewLineHorizontalDivider, getSecondaryDarkColor())

                if (status?.lowercase() == "delivered") {
                    setCardBackground(tvOrderPlaces, 100f, 0, getGreenColor())
                } else if (status?.lowercase() == "cancelled") {
                    setCardBackground(tvOrderPlaces, 100f, 0, getErrorColor())
                } else {
                    setCardBackground(tvOrderPlaces, 100f, 0, getPrimaryColor())
                }
                tvOrderPlaces.text = status
            }

            colorResources.getStringResource().apply {
                val orderId = "$orderId:"
                tvOrderTitle.text = orderId
                tvDispatchTitle.text = dispatchId
                tvModelTitle.text = if (assignedDriverId.isNullOrEmpty()) noDriverAssigned else assignedDriverId
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
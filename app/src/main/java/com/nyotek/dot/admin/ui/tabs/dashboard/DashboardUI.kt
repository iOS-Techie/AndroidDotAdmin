package com.nyotek.dot.admin.ui.tabs.dashboard

import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutAssignedListBinding
import com.nyotek.dot.admin.databinding.NsFragmentDashboardTabBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardUI @Inject constructor(private val binding: NsFragmentDashboardTabBinding, private val colorResources: ColorResources) {

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

    fun setDispatchOrderUI(bind: LayoutAssignedListBinding, selectedStatus: String, assignedDriverId: String?) {
        bind.apply {
            colorResources.apply {
                setCardBackground(clDispatchDetailView, 8f, width = 1)
                setBackground(viewLineDivider, colorResources.getSecondaryDarkColor())
                setBackground(viewLineHorizontalDivider, getSecondaryDarkColor())

                if (selectedStatus.lowercase() == "delivered") {
                    setCardBackground(tvOrderPlaces, 100f, 0, getGreenColor())
                } else if (selectedStatus.lowercase() == "cancelled") {
                    setCardBackground(tvOrderPlaces, 100f, 0, getErrorColor())
                } else {
                    setCardBackground(tvOrderPlaces, 100f, 0, getPrimaryColor())
                }
            }

            colorResources.getStringResource().apply {
                tvModelTitle.text = if (assignedDriverId.isNullOrEmpty()) noDriverAssigned else assignedDriverId

                val orderId = "$orderId:"
                tvOrderTitle.text = orderId
                tvDispatchTitle.text = dispatchId
            }
        }
    }
}
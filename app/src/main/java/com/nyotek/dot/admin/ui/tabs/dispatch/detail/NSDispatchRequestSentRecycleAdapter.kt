package com.nyotek.dot.admin.ui.tabs.dispatch.detail

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.extension.capitalizeWord
import com.nyotek.dot.admin.databinding.LayoutDispatchRequestSentBinding
import com.nyotek.dot.admin.models.responses.DispatchRequestItem
import javax.inject.Inject

class NSDispatchRequestSentRecycleAdapter @Inject constructor(private val themeUI: DispatchDetailUI) : BaseViewBindingAdapter<LayoutDispatchRequestSentBinding, DispatchRequestItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDispatchRequestSentBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, _, _ ->
        binding.apply {
            response.apply {
                themeUI.setDispatchRequestSentUI(binding)
                layoutTop.apply {
                    layoutName.tvDetail.text = driverId
                    layoutNumber.tvDetail.text = distanceKm.toString()
                    layoutEmail.tvDetail.text = status?.capitalizeWord()
                }

                layoutBottom.apply {
                    layoutName.tvDetail.text = NSDateTimeHelper.getDateForDispatchView(createdAt)
                    layoutNumber.tvDetail.text = NSDateTimeHelper.getDateForDispatchViewTime(createdAt)
                    layoutEmail.tvDetail.text = NSDateTimeHelper.getDateForDispatchViewTime(notifExpiry)
                }
            }
        }
    }
)
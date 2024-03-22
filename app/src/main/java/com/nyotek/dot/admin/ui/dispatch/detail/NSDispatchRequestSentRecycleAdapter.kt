package com.nyotek.dot.admin.ui.dispatch.detail

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.utils.capitalizeWord
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutDispatchRequestSentBinding
import com.nyotek.dot.admin.repository.network.responses.DispatchRequestItem

class NSDispatchRequestSentRecycleAdapter() : BaseViewBindingAdapter<LayoutDispatchRequestSentBinding, DispatchRequestItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDispatchRequestSentBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource,_, _ ->
        binding.apply {
            response.apply {
                layoutTop.apply {
                    rlName.visible()
                    rlNumber.visible()
                    rlEmail.visible()

                    layoutName.apply {
                        tvItemTitle.text = stringResource.driverName
                        tvDetail.text = driverId
                    }

                    layoutNumber.apply {
                        tvItemTitle.text = stringResource.distanceKm
                        tvDetail.text = distanceKm.toString()
                    }


                    layoutEmail.apply {
                        tvItemTitle.text = stringResource.status
                        tvDetail.text = status?.capitalizeWord()
                    }
                }

                layoutBottom.apply {
                    rlName.visible()
                    rlNumber.visible()
                    rlEmail.visible()

                    layoutName.apply {
                        tvItemTitle.text = stringResource.date
                        tvDetail.text = NSDateTimeHelper.getDateForDispatchView(createdAt)
                    }

                    layoutNumber.apply {
                        tvItemTitle.text = stringResource.notificationCreated
                        tvDetail.text = NSDateTimeHelper.getDateForDispatchViewTime(createdAt)
                    }

                    layoutEmail.apply {
                        tvItemTitle.text = stringResource.notificationExpired
                        tvDetail.text = NSDateTimeHelper.getDateForDispatchViewTime(notifExpiry)
                    }
                }
            }
        }
    }
)
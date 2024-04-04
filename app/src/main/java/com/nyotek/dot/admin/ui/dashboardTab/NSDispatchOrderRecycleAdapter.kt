package com.nyotek.dot.admin.ui.dashboardTab

import android.widget.ImageView
import android.widget.TextView
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.capitalizeWord
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.setGlideWithHolder
import com.nyotek.dot.admin.common.utils.setTexts
import com.nyotek.dot.admin.databinding.LayoutAssignedListBinding
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NSDispatchOrderRecycleAdapter(
    private val vendorCallback: (String, ImageView, TextView) -> Unit
) : BaseViewBindingAdapter<LayoutAssignedListBinding, NSDispatchOrderListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutAssignedListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource,_, _ ->
        binding.apply {
            response.apply {
                ColorResources.setCardBackground(clDispatchDetailView, 8f, width = 1)
                ColorResources.setBackground(viewLineDivider, ColorResources.getSecondaryDarkColor())
                ColorResources.setBackground(viewLineHorizontalDivider, ColorResources.getSecondaryDarkColor())

                val orderId = stringResource.orderId + ":"
                tvOrderTitle.text = orderId
                tvDispatchTitle.text = stringResource.dispatchId
                tvOrderId.text = vendorSid
                tvDispatchId.text = rId
                val finalStatus = status.first().status
                ColorResources.apply {
                    if (finalStatus.lowercase() == "delivered") {
                        setCardBackground(tvOrderPlaces, 100f, 0, getGreenColor())
                    } else if (finalStatus.lowercase() == "cancelled") {
                        setCardBackground(tvOrderPlaces, 100f, 0, getErrorColor())
                    } else {
                        setCardBackground(tvOrderPlaces, 100f, 0, getPrimaryColor())
                    }
                }

                tvOrderPlaces.text = finalStatus.capitalizeWord()

                userMetadata?.apply {
                    tvDriverTitle.text = userName
                    tvDescription.text = userPhone
                }

                tvModelTitle.text = if (assignedDriverId.isNullOrEmpty()) stringResource.noDriverAssigned else assignedDriverId
                tvModelDescription.text = ""

                if (isThirdParty == true) {
                    dispatchViewTitle.getMapValue(vendorName)
                    ivHubzIcon.setGlideWithHolder(vendorLogoUrl, "fit", 200)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        vendorCallback.invoke(vendorId?:"", ivHubzIcon, dispatchViewTitle)
                    }
                }

                tvStartingLocation.setTexts(pickup?.addressLine)
                tvEndingLocation.setTexts(destination?.addressLine)
            }
        }
    }
)
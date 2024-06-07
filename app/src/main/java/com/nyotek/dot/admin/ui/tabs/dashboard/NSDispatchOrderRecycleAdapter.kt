package com.nyotek.dot.admin.ui.tabs.dashboard

import android.widget.ImageView
import android.widget.TextView
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.extension.capitalizeWord
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.setCoil
import com.nyotek.dot.admin.common.extension.setTexts
import com.nyotek.dot.admin.databinding.LayoutAssignedListBinding
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NSDispatchOrderRecycleAdapter(
    private val themeUI: DashboardUI,
    private val vendorCallback: (String, ImageView, TextView) -> Unit
) : BaseViewBindingAdapter<LayoutAssignedListBinding, NSDispatchOrderListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutAssignedListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _,_, _ ->
        binding.apply {
            response.apply {
                themeUI.setDispatchOrderUI(binding, status.first().status, assignedDriverId)

                tvOrderId.text = vendorSid
                tvDispatchId.text = rId
                val finalStatus = status.first().status
                tvOrderPlaces.text = finalStatus.capitalizeWord()

                userMetadata?.apply {
                    tvDriverTitle.text = userName
                    tvDescription.text = userPhone
                }

                tvModelDescription.text = ""

                if (isThirdParty == true) {
                    dispatchViewTitle.getMapValue(vendorName)
                    ivHubzIcon.setCoil(vendorLogoUrl, "fit")
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
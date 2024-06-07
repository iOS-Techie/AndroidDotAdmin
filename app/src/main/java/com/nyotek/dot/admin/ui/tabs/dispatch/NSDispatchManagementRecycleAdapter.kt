package com.nyotek.dot.admin.ui.tabs.dispatch

import android.widget.ImageView
import android.widget.TextView
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.setCoil
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.setTexts
import com.nyotek.dot.admin.databinding.LayoutDispatchListBinding
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListData

class NSDispatchManagementRecycleAdapter(
    private val themeUI: DispatchUI,
    private val vendorCallback: (String, ImageView, TextView) -> Unit,
    private val callback: (NSDispatchOrderListData, Int) -> Unit
) : BaseViewBindingAdapter<LayoutDispatchListBinding, NSDispatchOrderListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDispatchListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _ ,position , _ ->
        binding.apply {
            response.apply {
                themeUI.setDispatchAdapterUI(binding, status.first().status, assignedDriverId)

                tvOrderId.text = vendorSid
                tvDispatchId.text = rId

                userMetadata?.apply {
                    tvDriverTitle.text = userName
                    tvDescription.text = userPhone
                }

                tvModelDescription.text = ""

                if (isThirdParty == true) {
                    dispatchViewTitle.getMapValue(vendorName)
                    ivHubzIcon.setCoil(vendorLogoUrl, "fit")
                } else {
                    vendorCallback.invoke(vendorId?:"", ivHubzIcon, dispatchViewTitle)
                }

                tvStartingLocation.setTexts(pickup?.addressLine)
                tvEndingLocation.setTexts(destination?.addressLine)

                clDispatchView.setSafeOnClickListener {
                    callback.invoke(this, position)
                }
            }
        }
    }
)
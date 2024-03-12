package com.nyotek.dot.admin.ui.dispatch

import android.widget.ImageView
import android.widget.TextView
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setTexts
import com.nyotek.dot.admin.databinding.LayoutDispatchListBinding
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private var selectedServiceLogo: String? = null

class NSDispatchManagementRecycleAdapter(
    private val vendorCallback: (String, ImageView, TextView) -> Unit,
    private val callback: (NSDispatchOrderListData) -> Unit
) : BaseViewBindingAdapter<LayoutDispatchListBinding, NSDispatchOrderListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDispatchListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource,_ ->
        binding.apply {
            response.apply {
                ColorResources.setCardBackground(clDispatchDetailView, 8f, width = 1)
                ColorResources.setBackground(viewLineDivider, ColorResources.getSecondaryDarkColor())

                val orderId = stringResource.orderId + ":"
                tvOrderTitle.text = orderId
                tvOrderId.text = rId
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

                //val capitalizedText = finalStatus//NSUtilities.capitalizeFirstLetter(finalStatus.replace("_", " "))
                tvOrderPlaces.text = finalStatus

                userMetadata?.apply {
                    tvDriverTitle.text = userName
                    tvDescription.text = userPhone
                }

                tvModelTitle.text = assignedDriverId
                tvModelDescription.text = ""

                CoroutineScope(Dispatchers.IO).launch {
                    vendorCallback.invoke(vendorId?:"", ivHubzIcon, dispatchViewTitle)
                }

                tvStartingLocation.setTexts(pickup?.addressLine)
                tvEndingLocation.setTexts(destination?.addressLine)

                clDispatchView.setSafeOnClickListener {
                    callback.invoke(this)
                }
            }
        }
    }
) {
    fun setServiceLogo(logo: String?) {
        selectedServiceLogo = logo
    }
}
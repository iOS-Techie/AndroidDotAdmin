package com.nyotek.dot.admin.ui.dispatch

import android.app.Activity
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.setGlideWithPlaceHolder
import com.nyotek.dot.admin.common.utils.setGlideWithOutPlace
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.databinding.LayoutDispatchListBinding
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData

private var selectedServiceLogo: String? = null

class NSDispatchManagementRecycleAdapter(
    private val activity: Activity,
    private val callback: ((NSDispatchOrderListData) -> Unit)
) : BaseViewBindingAdapter<LayoutDispatchListBinding, NSDispatchOrderListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDispatchListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _,_ ->
        binding.apply {
            response.apply {
                ColorResources.setCardBackground(clDispatchDetailView, 8f, width = 1)
                ColorResources.setBackground(viewLineDivider, ColorResources.getSecondaryDarkColor())
                dispatchViewTitle.text = response.rId
                ivHubzIcon.setGlideWithOutPlace(selectedServiceLogo)
                val finalStatus = response.status.first().status
                ColorResources.apply {
                    if (finalStatus.lowercase() == "delivered") {
                        setCardBackground(tvOrderPlaces, 100f, 0, getGreenColor())
                    } else if (finalStatus.lowercase() == "cancelled") {
                        setCardBackground(tvOrderPlaces, 100f, 0, getErrorColor())
                    } else {
                        setCardBackground(tvOrderPlaces, 100f, 0, getPrimaryColor())
                    }
                }

                tvOrderPlaces.text = NSUtilities.capitalizeFirstLetter(finalStatus.replace("_", " "))
                tvDriverTitle.text = response.userMetadata?.userName
                tvDescription.text = response.userMetadata?.userPhone
                tvModelTitle.text = response.assignedDriverId
                tvModelDescription.text = ""
                //val brandUrl = response.mediaUrl["0"]
                //ivHubzIcon.setGlideWithOutPlace(brandUrl)
                ivProductIconRound.setGlideWithPlaceHolder(
                    activity,
                    response.vendorLogoUrl,
                    R.drawable.ic_place_holder_product
                )
                tvOrderTitle.getMapValue(response.vendorName)
                tvStartingLocation.text = response.pickup?.addressLine
                tvEndingLocation.text = response.destination?.addressLine

                clDispatchView.setSafeOnClickListener {
                    callback.invoke(response)
                }
            }
        }
    }
) {
    fun setServiceLogo(logo: String?) {
        selectedServiceLogo = logo
    }
}
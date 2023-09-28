package com.nyotek.dot.admin.ui.dispatch

import android.app.Activity
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.glide
import com.nyotek.dot.admin.common.utils.glide200
import com.nyotek.dot.admin.common.utils.setGlideRound
import com.nyotek.dot.admin.common.utils.setGlideWithOutPlace
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutAssignedListBinding
import com.nyotek.dot.admin.databinding.LayoutDispatchListBinding
import com.nyotek.dot.admin.databinding.LayoutFleetItemBinding
import com.nyotek.dot.admin.repository.network.responses.FleetData
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData

class NSDispatchManagementRecycleAdapter(
    private val activity: Activity,
    private val callback: ((NSDispatchOrderListData, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutDispatchListBinding, NSDispatchOrderListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutDispatchListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource,_ ->
        binding.apply {
            response.apply {
                ColorResources.setCardBackground(clDispatchDetailView, 8f, width = 1)
                ColorResources.setBackground(viewLineDivider, ColorResources.getSecondaryDarkColor())
                dispatchViewTitle.text = response.rId
                tvOrderPlaces.text = NSUtilities.capitalizeFirstLetter(response.status.first().status.replace("_", " "))
                tvDriverTitle.text = response.userMetadata?.userName
                tvDescription.text = response.userMetadata?.userPhone
                tvModelTitle.text = response.assignedDriverId
                tvModelDescription.text = ""
                //val brandUrl = response.mediaUrl["0"]
                //ivHubzIcon.setGlideWithOutPlace(brandUrl)
                ivProductIconRound.setGlideRound(activity, response.vendorLogoUrl, R.drawable.ic_place_holder_product, "fill", 100)
                tvOrderTitle.getMapValue(response.vendorName)
                tvStartingLocation.text = response.pickup?.addressLine
                tvEndingLocation.text = response.destination?.addressLine
            }
        }
    }
)
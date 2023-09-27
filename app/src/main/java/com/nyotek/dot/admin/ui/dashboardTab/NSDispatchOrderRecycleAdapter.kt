package com.nyotek.dot.admin.ui.dashboardTab

import android.app.Activity
import com.bumptech.glide.Glide
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.glide
import com.nyotek.dot.admin.common.utils.setGlideRound
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.status
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutAssignedListBinding
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesBinding
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.NSDispatchOrderListData

class NSDispatchOrderRecycleAdapter(
    private val activity: Activity,
    private val callback: ((NSDispatchOrderListData, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutAssignedListBinding, NSDispatchOrderListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutAssignedListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource,_ ->
        binding.apply {
            response.apply {
                ColorResources.setCardBackground(clDispatchDetailView, 8f, width = 1)
                ColorResources.setBackground(viewLineDivider, ColorResources.getSecondaryDarkColor())
                dispatchViewTitle.text = response.rId
                tvOrderPlaces.text = NSUtilities.capitalizeFirstLetter(response.status[0].status)
                tvDriverTitle.text = response.userMetadata?.userName
                tvDescription.text = response.userMetadata?.userPhone
                tvModelTitle.text = response.assignedDriverId
                tvModelDescription.text = ""
                ivProductIconRound.setGlideRound(activity, response.vendorLogoUrl, R.drawable.ic_place_holder_product, "fill", 100)
                tvOrderTitle.getMapValue(response.vendorName)
                tvStartingLocation.text = response.pickup?.addressLine
                tvEndingLocation.text = response.destination?.addressLine
            }
        }
    }
)
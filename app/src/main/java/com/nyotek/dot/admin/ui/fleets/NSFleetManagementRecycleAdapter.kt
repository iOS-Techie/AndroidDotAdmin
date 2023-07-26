package com.nyotek.dot.admin.ui.fleets

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.callbacks.NSFleetDetailCallback
import com.nyotek.dot.admin.common.callbacks.NSSwitchEnableDisableCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.databinding.LayoutFleetItemBinding
import com.nyotek.dot.admin.repository.network.responses.FleetData

class NSFleetManagementRecycleAdapter(
    private val activity: Activity,
    private val isLanguageSelected: Boolean,
    private val fleetDetailCallback: NSFleetDetailCallback,
    private val switchEnableDisableCallback: NSSwitchEnableDisableCallback
) : BaseAdapter() {
    private val itemList: MutableList<FleetData> = arrayListOf()

    fun updateData(userList: MutableList<FleetData>) {
        itemList.clear()
        itemList.addAll(userList)
        notifyAdapter(this)
    }

    fun clearData() {
        itemList.clear()
        notifyAdapter(this)
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutFleetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSFleetViewHolder(view)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSFleetViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return itemList.size
    }

    /**
     * The view holder for fleet list
     *
     * @property binding The fleet list view binding
     */
    inner class NSFleetViewHolder(private val binding: LayoutFleetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: FleetData) {
            with(binding) {
                stringResource.apply {
                    tvItemActive.text = if (response.isActive) active else inActive
                    tvViewMore.text = view
                }
                tvItemTitle.text = getLngValue(response.name)
                if (response.logo.isNullOrEmpty()) {
                    ivVendor.setImageResource(R.drawable.ic_place_holder_img)
                } else {
                    Glide.with(activity.applicationContext).load(response.logo).apply(
                        RequestOptions().transform(
                            if (response.logoScale.equals(NSConstants.FILL)) CenterCrop() else FitCenter(),
                            RoundedCorners(20)
                        ).override(200, 200)
                    ).placeholder(R.drawable.ic_place_holder_img)
                        .error(R.drawable.ic_place_holder_img).into(ivVendor)
                }
                NSUtilities.switchEnableDisable(switchFleet, response.isActive)

                ivArrow.rotation = if (isLanguageSelected) 180f else 0f
                clViewMoreVendor.setOnClickListener {
                    fleetDetailCallback.onItemSelect(response)
                }

                switchFleet.setOnClickListener {
                    tvItemActive.text = if (response.isActive) stringResource.inActive else stringResource.active
                    NSUtilities.switchEnableDisable(switchFleet, !response.isActive)
                    switchEnableDisableCallback.switch(response.vendorId!!, !response.isActive)
                    response.isActive = !response.isActive
                }
            }
        }
    }
}
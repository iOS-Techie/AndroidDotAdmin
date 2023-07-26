package com.nyotek.dot.admin.ui.fleets.vehicle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.callbacks.NSVehicleSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSEditVehicleCallback
import com.nyotek.dot.admin.common.callbacks.NSSwitchEnableDisableCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.databinding.LayoutVehicleListItemBinding
import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem

class NSVehicleRecycleAdapter(
    private val context: Context,
    private val editVehicleCallback: NSEditVehicleCallback,
    private val switchEnableDisableCallback: NSSwitchEnableDisableCallback,
    private val vehicleItemSelect: NSVehicleSelectCallback
) : BaseAdapter() {
    private val itemList: MutableList<VehicleDataItem> = arrayListOf()

    fun updateData(branchList: MutableList<VehicleDataItem>) {
        itemList.clear()
        itemList.addAll(branchList)
        notifyAdapter(this)
    }

    fun notifyData() {
        notifyAdapter(this)
    }

    fun clearData() {
        itemList.clear()
        notifyAdapter(this)
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutVehicleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSVendorManagementViewHolder(view)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSVendorManagementViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return itemList.size
    }

    /**
     * The view holder for driver list
     *
     * @property binding The driver list view binding
     */
    inner class NSVendorManagementViewHolder(private val binding: LayoutVehicleListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: VehicleDataItem) {
            with(binding) {
                Glide.with(context).load(response.vehicleImg).into(binding.ivVehicleImg)

                tvVehicleTitle.text = response.manufacturer
                val year = "- ${response.manufacturingYear}"
                tvDescription.text = year
                tvManufacturer.text = response.model

                NSUtilities.switchEnableDisable(switchService, response.isActive)

                switchService.setOnClickListener {
                    NSUtilities.switchEnableDisable(switchService, !response.isActive)
                    switchEnableDisableCallback.switch(response.id!!, !response.isActive)
                    response.isActive = !response.isActive
                }

                clVehicleItem2.setOnClickListener {
                    vehicleItemSelect.onItemSelect(response.id?:"")
                }

                ivEdit.setOnClickListener {
                    editVehicleCallback.editVehicle(response, absoluteAdapterPosition)
                }
            }
        }
    }
}
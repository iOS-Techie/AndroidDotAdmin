package com.nyotek.dot.admin.ui.fleets.vehicle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.callbacks.NSCapabilitiesCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesVehicleBinding
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesVehicleSmallBinding
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem

class NSCapabilitiesVehicleRecycleAdapter(
    private val callback: NSCapabilitiesCallback,
    private val isSmallLayout: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemList: MutableList<CapabilitiesDataItem> = arrayListOf()
    private var selectedList: MutableList<String> = arrayListOf()

    fun updateData(list: MutableList<CapabilitiesDataItem>, capabilityList: MutableList<String>) {
        itemList.clear()
        itemList.addAll(list)
        selectedList = capabilityList
        notifyAdapter(this)
    }

    fun clearData() {
        itemList.clear()
        notifyAdapter(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (isSmallLayout) {
            val view = LayoutCapabilitiesVehicleSmallBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            LayoutSmallViewHolder(view)
        } else {
            val view = LayoutCapabilitiesVehicleBinding.inflate(
                layoutInflater,
                parent,
                false
            )
            LayoutViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LayoutViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        } else if (holder is LayoutSmallViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    /**
     * The view holder for capabilities list
     *
     * @property binding The capabilities list view binding
     */
    inner class LayoutViewHolder(private val binding: LayoutCapabilitiesVehicleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * To bind the capability details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: CapabilitiesDataItem) {
            with(binding) {
                setLayoutData(viewStatus, response, tvCapabilitiesTitle, cbCapability, clCapabilities)
            }
        }
    }

    /**
     * The view holder for capabilities list
     *
     * @property binding The capabilities list view binding
     */
    inner class LayoutSmallViewHolder(private val binding: LayoutCapabilitiesVehicleSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * To bind the capability details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: CapabilitiesDataItem) {
            with(binding) {
                setLayoutData(viewStatus, response, tvCapabilitiesTitle, cbCapability, clCapabilities)
            }
        }
    }

    private fun setLayoutData(viewStatus: View, response: CapabilitiesDataItem, tvTitle: TextView, cbCapability: CheckBox, clCapabilities: ConstraintLayout) {
        ColorResources.setCardBackground(viewStatus, 100f, 0, if (response.isActive) ColorResources.getGreenColor() else ColorResources.getGrayColor())
        tvTitle.getMapValue(response.label)

        if (selectedList.contains(response.id)) {
            cbCapability.isChecked = true
            callback.onItemSelect(response, !cbCapability.isChecked)
        }

        clCapabilities.setOnClickListener {
            cbCapability.isChecked = !cbCapability.isChecked
            callback.onItemSelect(response, !cbCapability.isChecked)
        }
    }
}
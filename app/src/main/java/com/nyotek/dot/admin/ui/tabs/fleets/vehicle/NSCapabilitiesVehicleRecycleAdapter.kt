package com.nyotek.dot.admin.ui.tabs.fleets.vehicle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.notifyAdapter
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesVehicleBinding
import com.nyotek.dot.admin.databinding.LayoutCapabilitiesVehicleSmallBinding
import com.nyotek.dot.admin.models.responses.CapabilitiesDataItem

class NSCapabilitiesVehicleRecycleAdapter(
    private val vehicleModel: BaseViewModel,
    private val callback: ((CapabilitiesDataItem, Boolean) -> Unit),
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
            holder.bind(itemList[holder.adapterPosition])
        } else if (holder is LayoutSmallViewHolder) {
            holder.bind(itemList[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class LayoutViewHolder(private val binding: LayoutCapabilitiesVehicleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(response: CapabilitiesDataItem) {
            with(binding) {
                setLayoutData(viewStatus, response, tvCapabilitiesTitle, cbCapability, clCapabilities)
            }
        }
    }

    inner class LayoutSmallViewHolder(private val binding: LayoutCapabilitiesVehicleSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(response: CapabilitiesDataItem) {
            with(binding) {
                setLayoutData(viewStatus, response, tvCapabilitiesTitle, cbCapability, clCapabilities)
            }
        }
    }

    private fun setLayoutData(viewStatus: View, response: CapabilitiesDataItem, tvTitle: TextView, cbCapability: CheckBox, clCapabilities: ConstraintLayout) {
        vehicleModel.colorResources.setCardBackground(viewStatus, 100f, 0, if (response.isActive) vehicleModel.colorResources.getGreenColor() else vehicleModel.colorResources.getGrayColor())
        tvTitle.getMapValue(response.label)

        if (selectedList.contains(response.id)) {
            cbCapability.isChecked = true
        }

        clCapabilities.setOnClickListener {
            cbCapability.isChecked = !cbCapability.isChecked
            callback.invoke(response, !cbCapability.isChecked)
        }
    }
}
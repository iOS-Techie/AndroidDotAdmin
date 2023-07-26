package com.nyotek.dot.admin.ui.fleets.detail

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.callbacks.NSServiceSelectCallback
import com.nyotek.dot.admin.common.utils.*
import com.nyotek.dot.admin.databinding.LayoutServiceListHorizontalBinding
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.FleetData

class NSFleetServiceListRecycleAdapter(
    val activity: Activity, var dialog: Boolean, var callback: NSServiceSelectCallback
) : BaseAdapter() {
    private val itemList: MutableList<NSGetServiceListData> = arrayListOf()
    private var selectedFleetData: FleetData? = null
    private var selectedVendorData1: NSCreateCompanyRequest? = null
    private var serviceList: MutableList<String> = arrayListOf()
    private var isVendorDetailCheck: Boolean = false

    fun updateData(userList: MutableList<NSGetServiceListData>, fleetData: FleetData?) {
        isVendorDetailCheck = true
        selectedFleetData = fleetData
        serviceList.clear()
        serviceList.addAll(fleetData?.serviceIds?: arrayListOf())
        itemList.clear()
        itemList.addAll(userList)
        notifyAdapter(this)
    }

    fun updateData2(userList: MutableList<NSGetServiceListData>, vendorData: NSCreateCompanyRequest?) {
        isVendorDetailCheck = false
        selectedVendorData1 = vendorData
        serviceList.clear()
        serviceList.addAll(vendorData?.serviceIds?: arrayListOf())
        itemList.clear()
        itemList.addAll(userList)
        notifyAdapter(this)
    }

    fun clearData() {
        itemList.clear()
        notifyAdapter(this)
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutServiceListHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSVendorDetailProfileViewHolder(view)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSVendorDetailProfileViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return itemList.size
    }

    /**
     * The view holder for vendor list
     *
     * @property binding The vendor list view binding
     */
    inner class NSVendorDetailProfileViewHolder(private val binding: LayoutServiceListHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSGetServiceListData) {
            with(binding) {
                cbCheck.isChecked = serviceList.contains(response.serviceId)
                tvTitle.text = response.name
                ColorResources.setBackground(viewDivider, ColorResources.getBackgroundColor())
                ColorResources.setCardBackground(viewActive, 100f, 0, if (response.isActive) ColorResources.getGreenColor() else ColorResources.getPrimaryColor())
                viewActive.alpha = if (response.isActive) 1f else 0.6f
                tvTitle.alpha = if (response.isActive) 1f else 0.6f
                if (absoluteAdapterPosition == itemList.size - 1) {
                    viewDivider.gone()
                } else {
                    viewDivider.visible()
                }

                if (isVendorDetailCheck) {
                    cbCheck.isChecked =
                        selectedFleetData?.serviceIds?.contains(response.serviceId) == true
                } else {
                    cbCheck.isChecked =
                        selectedVendorData1?.serviceIds?.contains(response.serviceId) == true
                }
                clCheck.setOnClickListener {
                    if (cbCheck.isChecked) {
                        cbCheck.isChecked = false
                        if(dialog) {
                            selectedVendorData1?.serviceIds?.remove(response.serviceId)
                        }else{
                            selectedFleetData?.serviceIds?.remove(response.serviceId)
                        }
                    } else {
                        cbCheck.isChecked = true
                        if (response.serviceId?.isNotEmpty() == true) {

                            if(dialog) {
                                selectedVendorData1?.serviceIds?.add(response.serviceId)
                            }else{
                                selectedFleetData?.serviceIds?.add(response.serviceId)
                            }
                        }
                    }
                    callback.onItemSelect(response.serviceId?:"", cbCheck.isChecked)
                }
            }
        }
    }
}
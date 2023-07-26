package com.nyotek.dot.admin.ui.fleets.employee

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.callbacks.NSVehicleSelectCallback
import com.nyotek.dot.admin.common.callbacks.NSEmployeeCallback
import com.nyotek.dot.admin.common.callbacks.NSEmployeeSwitchEnableDisableCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.getLngValue
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.databinding.LayoutEmployeeListBinding
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem

class NSEmployeeRecycleAdapter(
    private val activity: Activity,
    private val callback: NSEmployeeCallback,
    private val switchEnableDisableCallback: NSEmployeeSwitchEnableDisableCallback,
    private val branchItemSelect: NSVehicleSelectCallback
) : BaseAdapter() {
    private val itemList: MutableList<EmployeeDataItem> = arrayListOf()
    private var jobMap: HashMap<String, JobListDataItem> = hashMapOf()

    fun updateData(userList: MutableList<EmployeeDataItem>, map: HashMap<String, JobListDataItem>) {
        clearData()
        jobMap = map
        itemList.addAll(userList)
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
        val view = LayoutEmployeeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class NSVendorManagementViewHolder(private val binding: LayoutEmployeeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: EmployeeDataItem) {
            with(binding) {

                tvDescription.text = getLngValue(jobMap[response.titleId]?.name)
                tvEmployeeTitle.text = response.userId

                NSUtilities.switchEnableDisable(switchService, response.isActive)
                ColorResources.setBackground(clEmployeeItem, if (response.isEmployeeSelected) ColorResources.getBackgroundColor() else ColorResources.getWhiteColor())

                switchService.setOnClickListener {
                    NSUtilities.switchEnableDisable(switchService, !response.isActive)
                    switchEnableDisableCallback.switch(response.vendorId!!, response.userId!!, !response.isActive)
                    response.isActive = !response.isActive
                }

                ivDelete.setOnClickListener {
                    callback.onClick(response, true)
                }

                ivEdit.setOnClickListener {
                    callback.onClick(response, false)
                }

                clEmployeeItem.setOnClickListener {
                    branchItemSelect.onItemSelect(response.vendorId?:"")
                }
            }
        }
    }
}
package com.nyotek.dot.admin.ui.fleets.employee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.databinding.LayoutEmployeeSearchUserBinding
import com.nyotek.dot.admin.repository.network.responses.NSUserDetail

class NSEmployeeUserSearchRecycleAdapter : BaseAdapter() {
    private val itemList: MutableList<NSUserDetail> = arrayListOf()
    private var selectedId: String? = ""

    fun updateData(userList: MutableList<NSUserDetail>) {
        itemList.clear()
        itemList.addAll(userList)
        notifyAdapter(this)
    }

    fun clearData() {
        itemList.clear()
        notifyAdapter(this)
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutEmployeeSearchUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSSideNavigationViewHolder(orderView)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSSideNavigationViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return itemList.size
    }

    /**
     * The view holder for order list
     *
     * @property employeeBinding The order list view binding
     */
    inner class NSSideNavigationViewHolder(private val employeeBinding: LayoutEmployeeSearchUserBinding) :
        RecyclerView.ViewHolder(employeeBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSUserDetail) {
            with(employeeBinding) {
                if (selectedId == null || response.id != selectedId ) {
                    response.isEmployeeSelected = false
                }
                ivCorrect.setVisibility(response.isEmployeeSelected)
                tvItemTitle.text = response.username
                clItemSelect.setOnClickListener {
                    selectedId = response.id
                    response.isEmployeeSelected = !response.isEmployeeSelected
                    notifyAdapter(this@NSEmployeeUserSearchRecycleAdapter)
                }
            }
        }
    }
}
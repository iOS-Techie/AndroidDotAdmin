package com.nyotek.dot.admin.common

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.linearHorizontal
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import com.nyotek.dot.admin.ui.fleets.NSCommonFilterRecycleAdapter

class FilterHelper(private val activity: Activity, private val recycleView: RecyclerView, filterList: MutableList<ActiveInActiveFilter>, private val callback: ((ActiveInActiveFilter, MutableList<ActiveInActiveFilter>) -> Unit)) {

    private var serviceFilterRecycleAdapter: NSCommonFilterRecycleAdapter? = null
    init {
        setVendorFilterList(filterList)
        //setFilterTypes()
    }

    companion object {
        private val stringResource = StringResourceResponse()
        fun getCommonFilterLists(): MutableList<ActiveInActiveFilter> {
            val filterList: MutableList<ActiveInActiveFilter> = arrayListOf()
            filterList.add(ActiveInActiveFilter(stringResource.all, NSConstants.ALL, true))
            filterList.add(ActiveInActiveFilter(stringResource.active, NSConstants.ACTIVE, false))
            filterList.add(ActiveInActiveFilter(stringResource.inActive, NSConstants.IN_ACTIVE, false))
            return filterList
        }

        fun getDispatchFilterLists(): MutableList<ActiveInActiveFilter> {
            val filterList: MutableList<ActiveInActiveFilter> = arrayListOf()
            filterList.add(ActiveInActiveFilter(stringResource.all, NSConstants.ALL, true))
            filterList.add(ActiveInActiveFilter("New", NSConstants.ACTIVE, false))
            filterList.add(ActiveInActiveFilter("Ready For Pickup", NSConstants.IN_ACTIVE, false))
            filterList.add(ActiveInActiveFilter("Picked Up", NSConstants.IN_ACTIVE, false))
            filterList.add(ActiveInActiveFilter("Delivered", NSConstants.IN_ACTIVE, false))
            filterList.add(ActiveInActiveFilter("Cancelled", NSConstants.IN_ACTIVE, false))
            return filterList
        }
    }


    private fun setVendorFilterList(filterList: MutableList<ActiveInActiveFilter>) {
        recycleView.setVisibility(filterList.isValidList())
        if (filterList.isValidList()) {
            recycleView.apply {
                linearHorizontal(activity)
                serviceFilterRecycleAdapter = NSCommonFilterRecycleAdapter { model, list ->
                    callback.invoke(model, list)
                    notifyAdapter(serviceFilterRecycleAdapter!!)
                }
                adapter = serviceFilterRecycleAdapter
                isNestedScrollingEnabled = false
                serviceFilterRecycleAdapter?.setData(filterList)
                serviceFilterRecycleAdapter?.setList()
            }
        }
    }
}
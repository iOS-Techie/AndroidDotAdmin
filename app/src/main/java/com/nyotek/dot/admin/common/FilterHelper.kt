package com.nyotek.dot.admin.common

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.linearHorizontal
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter
import com.nyotek.dot.admin.ui.fleets.NSCommonFilterRecycleAdapter

class FilterHelper(private val activity: Activity, private val recycleView: RecyclerView, private val callback: ((ActiveInActiveFilter, MutableList<ActiveInActiveFilter>) -> Unit)) {

    private val stringResource = NSApplication.getInstance().getStringModel()

    init {
        setFilterTypes()
    }

    private fun setFilterTypes() {
        val filterList: MutableList<ActiveInActiveFilter> = arrayListOf()
        filterList.add(ActiveInActiveFilter(stringResource.all, NSConstants.ALL, true))
        filterList.add(ActiveInActiveFilter(stringResource.active, NSConstants.ACTIVE, false))
        filterList.add(ActiveInActiveFilter(stringResource.inActive, NSConstants.IN_ACTIVE, false))
        setVendorFilterList(filterList)
    }

    private fun setVendorFilterList(filterList: MutableList<ActiveInActiveFilter>) {
        recycleView.setVisibility(filterList.isValidList())
        if (filterList.isValidList()) {
            recycleView.apply {
                linearHorizontal(activity)
                val serviceFilterRecycleAdapter = NSCommonFilterRecycleAdapter { model, list ->
                    callback.invoke(model, list)
                    notifyAdapter(adapter!!)
                }
                adapter = serviceFilterRecycleAdapter
                isNestedScrollingEnabled = false
                serviceFilterRecycleAdapter.setData(filterList)
                serviceFilterRecycleAdapter.setList()
            }
        }
    }
}
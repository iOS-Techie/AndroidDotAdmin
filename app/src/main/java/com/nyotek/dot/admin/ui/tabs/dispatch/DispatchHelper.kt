package com.nyotek.dot.admin.ui.tabs.dispatch

import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.models.responses.NSDispatchOrderListData

object DispatchHelper {
    private var dispatchList: MutableList<NSDispatchOrderListData>  = arrayListOf()
    private var isCancelled: Boolean = false

    fun setDispatchList(list: MutableList<NSDispatchOrderListData>) {
        dispatchList = list
    }

    fun getDispatchList(): MutableList<NSDispatchOrderListData> {
        return dispatchList
    }

    fun updateDispatchItem(dispatchId: String?, item: NSDispatchOrderListData?) {
        if (dispatchList.isValidList()) {
            if (dispatchId != null && item != null) {
                val index = dispatchList.indexOfFirst { it.dispatchId == dispatchId }
                dispatchList[index] = item
            }
        }
    }

    fun setCancelled(isCancel: Boolean) {
        isCancelled = isCancel
    }

    fun isCancelledStatus(): Boolean {
        return isCancelled
    }
}
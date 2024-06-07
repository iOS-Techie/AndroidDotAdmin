package com.nyotek.dot.admin.ui.tabs.fleets.employee

import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.models.responses.EmployeeDataItem

object EmployeeHelper {
    private var employeeList: MutableList<EmployeeDataItem>  = arrayListOf()

    fun setEmployeeList(list: MutableList<EmployeeDataItem>) {
        employeeList = list
    }

    fun getEmployeeList(): MutableList<EmployeeDataItem> {
        return employeeList
    }

    fun updateEmployeeItem(employeeId: String?, item: EmployeeDataItem?) {
        if (employeeList.isValidList()) {
            if (employeeId != null && item != null) {
                val index = employeeList.indexOfFirst { it.userId == employeeId }
                employeeList[index] = item
            }
        }
    }
}
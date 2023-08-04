package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem

interface NSEmployeeEditCallback {

    fun onEmployee(empDataItem: EmployeeDataItem)
}
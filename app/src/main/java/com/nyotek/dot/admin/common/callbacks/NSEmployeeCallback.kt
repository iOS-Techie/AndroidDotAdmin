package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem

/**
 * The interface to listen the click on side navigation item
 */
interface NSEmployeeCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onClick(employeeData: EmployeeDataItem, isDelete: Boolean)
}
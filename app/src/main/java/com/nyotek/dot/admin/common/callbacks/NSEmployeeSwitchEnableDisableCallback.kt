package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on side navigation item
 */
interface NSEmployeeSwitchEnableDisableCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun switch(vendorId: String, userId: String, isEnable: Boolean)
}
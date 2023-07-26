package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on side navigation item
 */
interface NSServiceSelectCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(serviceId: String, isChecked: Boolean)
}
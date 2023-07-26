package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on side navigation item
 */
interface NSVehicleSelectCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(vendorId: String)
}
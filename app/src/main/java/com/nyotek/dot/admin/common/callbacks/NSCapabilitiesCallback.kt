package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem

/**
 * The interface to listen the click on side navigation item
 */
interface NSCapabilitiesCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(model: CapabilitiesDataItem, isDelete: Boolean)
}
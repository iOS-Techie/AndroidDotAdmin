package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.FleetData

/**
 * The interface to listen the click on side navigation item
 */
interface NSFleetDetailCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(model: FleetData)
}
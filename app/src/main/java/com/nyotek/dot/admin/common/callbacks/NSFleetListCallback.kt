package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.FleetData

/**
 * The interface to listen the click on fleet item
 */
interface NSFleetListCallback {

    /**
     * Invoked when the fleet item click
     */
    fun onFleets(fleetList: MutableList<FleetData>)
}
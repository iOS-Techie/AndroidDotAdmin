package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on capability Update
 */
interface NSServiceCapabilityUpdateCallback {

    /**
     * Invoked when the capability Update
     */
    fun onItemSelect(serviceId: String, capabilityId: String, fleets: List<String>, isDirectFleet: Boolean, isFleetUpdate: Boolean)
}
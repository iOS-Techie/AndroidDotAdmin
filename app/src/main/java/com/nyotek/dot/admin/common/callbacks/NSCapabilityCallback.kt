package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem

/**
 * The interface to listen the click on capability item
 */
interface NSCapabilityCallback {

    /**
     * Invoked when the capability item click
     */
    fun onCapability(capabilities: MutableList<CapabilitiesDataItem>)
}
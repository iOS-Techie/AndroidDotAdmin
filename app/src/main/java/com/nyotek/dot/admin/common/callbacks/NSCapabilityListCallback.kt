package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on capability item
 */
interface NSCapabilityListCallback {

    /**
     * Invoked when the capability item click
     */
    fun onCapability(capabilities: MutableList<String>)
}
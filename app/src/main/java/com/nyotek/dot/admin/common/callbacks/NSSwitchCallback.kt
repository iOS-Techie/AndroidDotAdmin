package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on side navigation item
 */
interface NSSwitchCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun switch(serviceId: String, isEnable: Boolean)
}
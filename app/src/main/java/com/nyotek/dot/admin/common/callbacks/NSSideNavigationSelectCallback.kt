package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.NSNavigationResponse

/**
 * The interface to listen the click on side navigation item
 */
interface NSSideNavigationSelectCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(navResponse: NSNavigationResponse, position: Int)
}
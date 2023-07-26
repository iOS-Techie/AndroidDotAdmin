package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.NSCommonResponse

/**
 * The interface to listen the click on side navigation item
 */
interface NSLogoSelectCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(model: NSCommonResponse, position: Int)
}
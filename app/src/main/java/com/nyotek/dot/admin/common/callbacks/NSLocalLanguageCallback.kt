package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse

/**
 * The interface to listen the click on  local language item
 */
interface NSLocalLanguageCallback {

    /**
     * Invoked when the local language call
     */
    fun onItemSelect(model: NSLocalLanguageResponse)
}
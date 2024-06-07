package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.models.responses.NSLanguageStringResponse


/**
 * The interface to listen the get json
 */
interface NSLocalJsonCallback {

    /**
     * Invoked when the language string get
     */
    fun onLocal(json: NSLanguageStringResponse)
}
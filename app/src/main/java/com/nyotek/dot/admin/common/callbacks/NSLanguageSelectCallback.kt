package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on language item
 */
interface NSLanguageSelectCallback {

    /**
     * Invoked when the language item click
     */
    fun onPosition(position: Int)
}
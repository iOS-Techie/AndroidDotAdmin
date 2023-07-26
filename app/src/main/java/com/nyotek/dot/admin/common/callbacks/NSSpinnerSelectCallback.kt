package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on spinner selected item
 */
interface NSSpinnerSelectCallback {

    /**
     * Invoked when the spinner item click
     */
    fun onItemSelect(item: String)
}
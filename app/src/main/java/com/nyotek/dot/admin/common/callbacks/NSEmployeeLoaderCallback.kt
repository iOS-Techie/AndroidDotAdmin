package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on progress load item
 */
interface NSEmployeeLoaderCallback {

    /**
     * Invoked when the progress load
     */
    fun onLoad(isLoad: Boolean)
}
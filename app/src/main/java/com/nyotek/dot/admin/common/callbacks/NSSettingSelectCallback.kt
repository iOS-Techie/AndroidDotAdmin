package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on setting item
 */
interface NSSettingSelectCallback {

    /**
     * Invoked when the setting item click
     */
    fun onPosition(title: String)
}
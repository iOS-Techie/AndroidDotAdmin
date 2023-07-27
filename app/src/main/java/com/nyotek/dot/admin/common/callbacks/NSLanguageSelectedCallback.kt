package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on side navigation item
 */
interface NSLanguageSelectedCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onItemSelect(language: String, isNotify: Boolean = false)
}
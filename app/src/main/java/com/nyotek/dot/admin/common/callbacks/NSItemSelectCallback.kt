package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen the click on side navigation item
 */
interface NSItemSelectCallback {
    fun onItemSelect(selectedId: String)
}
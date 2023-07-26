package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.ActiveInActiveFilter

/**
 * The interface to listen the click on side navigation item
 */
interface NSFleetFilterCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun onFilterSelect(model: ActiveInActiveFilter, list: MutableList<ActiveInActiveFilter>)
}
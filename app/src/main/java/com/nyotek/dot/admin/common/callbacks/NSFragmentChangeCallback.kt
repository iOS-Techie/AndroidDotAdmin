package com.nyotek.dot.admin.common.callbacks

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

/**
 * The interface to listen the click on side navigation item
 */
interface NSFragmentChangeCallback {

    /**
     * Invoked when the side navigation item click
     */
    fun setFragment(previousFragmentName: String, fragment: Fragment, isBackStack: Boolean, bundle: Bundle = bundleOf())
}
package com.nyotek.dot.admin.common.callbacks

import androidx.fragment.app.Fragment

/**
 * The interface through which the fragment request the activity to replace the current fragment with the specified one.
 */
interface NSReplaceFragmentCallback {
    /**
     * Invoked when the fragment needs to be replaced
     *
     * @param fragmentToReplace    The fragment which should replace the current one
     * @param shouldAddToBackStack The boolean specifying whether to add the fragment to backstack or not
     * @param containerId          The id of the layout which acts as a container for the fragments
     */
    fun replaceCurrentFragment(
        fragmentToReplace: Fragment,
        shouldAddToBackStack: Boolean,
        containerId: Int
    )
}
package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen for updating the progress status from fragment to activity
 */
interface NSProgressCallback {
    /**
     * Invoked when the progress should be updated
     *
     * @param shouldShowProgress The boolean to determine whether to show or hide the progress bar
     */
    fun updateProgress(shouldShowProgress: Boolean)
}
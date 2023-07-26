package com.nyotek.dot.admin.common.callbacks

/**
 * The interface to listen for change pages
 */
interface NSNotificationPageChangeCallback {
    fun onPageChange(notificationId: String)
}
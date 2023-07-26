package com.nyotek.dot.admin.repository.network.callbacks

/**
 * The interface to listen the token need to refresh
 */
interface NSTokenRefreshCallback {
    fun onTokenRefresh()
}
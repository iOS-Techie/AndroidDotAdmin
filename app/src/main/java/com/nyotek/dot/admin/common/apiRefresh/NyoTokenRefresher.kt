package com.nyotek.dot.admin.common.apiRefresh

import android.util.Log
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object NyoTokenRefresher {

    private const val kRefreshingOffset: Long = 5 * 1000
    private var refreshJob: Job? = null

    // --------------------------------------
    // MARK: - Private
    // --------------------------------------

    private val _expiresDate: Long?
        get() {
            val unixEpochTime = NSApplication.getInstance().getPrefs().userData?.data?.expiresIn ?: return null
            return unixEpochTime/ 1000000
        }

    private val refreshingTimeInterval: Long?
        get() {
            val expiresDate = _expiresDate ?: return null
            val timeInterval = expiresDate - System.currentTimeMillis() - kRefreshingOffset
            return if (timeInterval > 0) timeInterval else null
        }

    // --------------------------------------
    // MARK: - Event
    // --------------------------------------

    private fun handleRefreshEvent() {

        if (!NSUserManager.isUserLoggedIn) return

        val token = NSApplication.getInstance().getPrefs().refreshToken
        if (!token.isNullOrEmpty()) {
            NSUserRepository.refreshToken(token, object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    Log.d("NyoTokenRefresher:", "refreshed token at: ${System.currentTimeMillis()} and valid till: ${NSApplication.getInstance().getPrefs().userData?.data?.expiresIn ?: "Unknown"}")
                }

                override fun onError(errors: List<Any>) {
                    Log.d("NyoTokenRefresher:" ,"refreshing token error: $errors")
                }

                override fun onFailure(failureMessage: String?) {

                }

                override fun <T> onNoNetwork(localData: T) {

                }
            })
        }
    }

    // --------------------------------------
    // MARK: - Public
    // --------------------------------------

    fun refreshIfNeeded() {
        if (!NSUserManager.isUserLoggedIn) return
        refreshJob = refreshingTimeInterval?.let { timeInterval ->
            CoroutineScope(Dispatchers.Main).launch {
                if (timeInterval > 0) {
                    validate()
                } else {
                    handleRefreshEvent()
                }
            }
        }
    }

    fun validate() {
        refreshJob = refreshingTimeInterval?.let { timeInterval ->
            CoroutineScope(Dispatchers.Main).launch {
                delay(timeInterval)
                handleRefreshEvent()
            }
        }
    }

    fun forceStop() {
        refreshJob?.cancel()
    }
}
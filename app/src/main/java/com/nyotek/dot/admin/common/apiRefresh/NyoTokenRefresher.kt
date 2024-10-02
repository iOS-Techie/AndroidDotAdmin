package com.nyotek.dot.admin.common.apiRefresh

import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSDataStorePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object NyoTokenRefresher {

    private const val kRefreshingOffset: Long = 5 * 1000
    private var refreshJob: Job? = null
    private var dataStoreRepository: NSDataStorePreferences? = null

    // --------------------------------------
    // MARK: - Private
    // --------------------------------------

    private val _expiresDate: Long?
        get() {
            val unixEpochTime = dataStoreRepository?.userData?.data?.expiresIn ?: return null
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

    private fun handleRefreshEvent(baseViewModel: BaseViewModel) {
        if (dataStoreRepository?.isUserLoggedIn == false) return
        baseViewModel.refreshToken()
    }

    // --------------------------------------
    // MARK: - Public
    // --------------------------------------

    fun refreshIfNeeded(storeRepository: NSDataStorePreferences, baseViewModel: BaseViewModel) {
        dataStoreRepository = storeRepository
        if (!storeRepository.isUserLoggedIn) return
        refreshJob = refreshingTimeInterval?.let { timeInterval ->
            CoroutineScope(Dispatchers.Main).launch {
                if (timeInterval > 0) {
                    validate(baseViewModel)
                } else {
                    handleRefreshEvent(baseViewModel)
                }
            }
        }
    }

    fun validate(baseViewModel: BaseViewModel) {
        refreshJob = refreshingTimeInterval?.let { timeInterval ->
            CoroutineScope(Dispatchers.Main).launch {
                delay(timeInterval)
                handleRefreshEvent(baseViewModel)
            }
        }
    }

    fun forceStop() {
        //refreshJob?.cancel()
    }
}
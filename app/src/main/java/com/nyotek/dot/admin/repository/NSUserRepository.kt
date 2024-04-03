package com.nyotek.dot.admin.repository

import android.util.Log
import com.nyotek.dot.admin.common.apiRefresh.NyoTokenRefresher
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSLoginRequest
import com.nyotek.dot.admin.repository.network.requests.NSRefreshTokenRequest
import com.nyotek.dot.admin.repository.network.responses.NSLogoutResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to user
 */
object NSUserRepository: BaseRepository() {

    /**
     * To make signUp to register new user
     *
     * @param email The email provided by the user
     * @param password The password provided by the user
     * @param viewModelCallback The callback to communicate back to view model
     */
    fun loginWithEmailPassword(email: String?, password: String?, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val loginRequest = NSLoginRequest(email, password)
            apiManager.loginWithEmailPassword(
                loginRequest,
                object : NSRetrofitCallback<NSUserResponse>(
                    viewModelCallback, NSApiErrorHandler.ERROR_LOGIN_WITH_EMAIL_PASSWORD
                ) {
                    override fun <T> onResponse(response: Response<T>) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val userResponse = response.body() as NSUserResponse
                            if (userResponse.data!!.refreshToken.isNullOrEmpty()) {
                                viewModelCallback.onFailure(stringResource.notRefreshToken)
                            } else {
                                NSUserManager.saveUserInPreference(response)
                                NSUserManager.saveHeadersInPreference(response)
                                NyoTokenRefresher.validate()
                                viewModelCallback.onSuccess(response.body())
                            }
                        }
                    }

                    override fun onRefreshToken() {
                        loginWithEmailPassword(email, password, viewModelCallback)
                    }
                })
        }
    }

    /**
     * To make refresh token
     *
     * @param token The phone number provided by the user
     * @param viewModelCallback The callback to communicate back to view model
     */
     fun refreshToken(token: String?, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val tokenRequest = NSRefreshTokenRequest(token)
            apiManager.refreshToken(tokenRequest, object : NSRetrofitCallback<NSUserResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_REFRESH_TOKEN
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val userResponse = response.body() as NSUserResponse
                        if (userResponse.data!!.refreshToken.isNullOrEmpty()) {
                            viewModelCallback.onFailure(stringResource.notRefreshToken)
                        } else {
                            NSUserManager.saveUserInPreference(response)
                            NSUserManager.saveHeadersInPreference(response)
                            NyoTokenRefresher.validate()
                            viewModelCallback.onSuccess(response.body())
                        }
                    }
                }

                override fun onRefreshToken() {
                    refreshToken(token, viewModelCallback)
                }
            })
        }
    }

    /**
     * To make logout
     *
     * @param viewModelCallback The callback to communicate back to view model
     */
    fun logout(viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.logout(object : NSRetrofitCallback<NSLogoutResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_LOGOUT
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        //NSApplication.getInstance().getPrefs().clearPrefData()
                        viewModelCallback.onSuccess(response.body()?:NSLogoutResponse())
                    }
                }

                override fun onRefreshToken() {
                    logout(viewModelCallback)
                }
            })
        }
    }

    /**
     * To get user detail
     *
     * @param viewModelCallback The callback to communicate back to view model
     */
    fun getUserDetail(viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getUserDetailData(object : NSRetrofitCallback<NSUserDetailResponse>(
                viewModelCallback, NSApiErrorHandler.ERROR_USER_DETAIL
            ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.d("UserDetailCall", "getUserMainDetail: 5")
                        viewModelCallback.onSuccess(response.body()?:NSUserDetailResponse())
                    }
                }

                override fun onRefreshToken() {
                    getUserDetail(viewModelCallback)
                }
            })
        }
    }
}

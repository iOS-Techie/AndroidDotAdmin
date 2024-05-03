package com.nyotek.dot.admin.common

import com.nyotek.dot.admin.repository.network.responses.NSDetailUser
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserResponse
import retrofit2.Response

object NSUserManager {
    private val prefs = NSApplication.getInstance().getPrefs()
    private var userDetail: NSDetailUser? = null

    //Status of user logged in
    val isUserLoggedIn: Boolean get() = !getAuthToken().isNullOrBlank()

    /**
     * To get authentication token
     */
    fun getAuthToken() = prefs.authToken

    /**
     * To save headers in preference
     *
     * @param T template of response
     * @param response response class
     */
    fun <T> saveHeadersInPreference(response: Response<T>) {
        val headers = response.body() as NSUserResponse
        prefs.authToken = headers.data!!.accessToken
        prefs.refreshToken = headers.data!!.refreshToken
    }

    /**
     * To save users response and headers in preference
     *
     * @param T template of response
     * @param response login response
     */
    fun <T> saveUserInPreference(response: Response<T>) {
        val userResponse = response.body() as NSUserResponse
        prefs.userData = userResponse
    }

    fun setUserDetail(user: NSDetailUser?) {
        userDetail = user
    }

    fun getUserDetail(): NSDetailUser? {
        return userDetail
    }
}
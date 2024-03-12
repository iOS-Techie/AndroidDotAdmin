package com.nyotek.dot.admin.repository.network.callbacks

import android.os.Handler
import android.os.Looper
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Callback that extends the Retrofit callback in order to perform actions other than API success or failure.
 *
 * @param T The type to accept
 * @property viewModelCallback The callback to communicate back to repository
 * @property errorResponseTag The error response tag to handle the appropriate error type
 */
abstract class NSRetrofitCallback<T>(
    private val viewModelCallback: NSGenericViewModelCallback,
    private val errorResponseTag: String? = null
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onResponse(response)
        } else {
            Handler(Looper.getMainLooper()).post {
                NSApiErrorHandler.getApiErrorMessage(response, viewModelCallback, object : NSTokenRefreshCallback {
                    override fun onTokenRefresh() {

                    }
                })
                onErrorResponse(response)
            }
        }
    }

    fun onSuspendResponse(response: Response<T>) {
        if (response.isSuccessful) {
            onResponse(response)
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                NSApiErrorHandler.getApiErrorMessage(response, viewModelCallback, object : NSTokenRefreshCallback {
                    override fun onTokenRefresh() {
                        refreshToken()
                    }
                })
            }
            onErrorResponse(response)
        }
    }

    private fun refreshToken() {
        val token = NSApplication.getInstance().getPrefs().refreshToken
        if (!token.isNullOrEmpty()) {
            NSUserRepository.refreshToken(token, object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    onRefreshToken()
                }

                override fun onError(errors: List<Any>) {
                    viewModelCallback.onError(errors)
                }

                override fun onFailure(failureMessage: String?) {
                    viewModelCallback.onFailure(failureMessage)
                }

                override fun <T> onNoNetwork(localData: T) {
                    onNoNetwork()
                }
            })
        }
    }

    /**
     * Called when response is failed
     */
    override fun onFailure(call: Call<T>, throwable: Throwable) {
        // We are cancelling all running api if user press back from any screen. So that time we don't need to show error alert with Something went wrong
        if (!call.isCanceled) {
            viewModelCallback.onFailure((if (throwable is UnknownHostException) NSConstants.UNKNOWN_HOST_EXCEPTION else null))
        }
    }

    /**
     * Called when response is success
     *
     * @param T template
     * @param response response of web request
     */
    abstract fun <T> onResponse(response: Response<T>)

    /**
     * Called when need to token refresh
     *
     */
    abstract fun onRefreshToken()

    /**
     * Called when response is error
     *
     * @param T template
     * @param response response of web request
     */
    open fun <T> onErrorResponse(response: Response<T>) {
        // will be overridden by extended callbacks to handle the error response
    }

    /**
     * Called when there is no network while making the web service call.
     */
    fun onNoNetwork() {
        viewModelCallback.onNoNetwork(null)
    }
}
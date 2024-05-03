package com.nyotek.dot.admin.repository

import com.google.gson.Gson
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSLanguageLocaleRequest
import com.nyotek.dot.admin.repository.network.requests.NSLanguageRequest
import com.nyotek.dot.admin.repository.network.requests.NSLanguageStringRequest
import com.nyotek.dot.admin.repository.network.responses.NSErrorResponse
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

object NSLanguageStringRepository : BaseRepository() {
    val gson = Gson()

    /**
     * To get languageString data API
     *
     */
    fun getLanguageString(languageStringRequest: NSLanguageStringRequest, viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.getLanguageString(languageStringRequest, object :
                NSRetrofitCallback<NSLanguageStringResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_GET_LANGUAGE_STRING
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?: NSLanguageStringResponse())
                    }
                }

                override fun onRefreshToken() {
                    getLanguageString(languageStringRequest, viewModelCallback)
                }
            })
        }
    }

    fun getLocalLanguages(serviceId: String, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val languageRequest = NSLanguageRequest(serviceId)
            apiManager.listLocalLanguages(
                languageRequest,
                object : NSRetrofitCallback<NSLocalLanguageResponse>(
                    viewModelCallback, NSApiErrorHandler.ERROR_LOCAL_LANGUAGE
                ) {
                    override fun <T> onResponse(response: Response<T>) {
                        CoroutineScope(Dispatchers.Main).launch {
                            viewModelCallback.onSuccess(response.body())
                        }
                    }

                    override fun onRefreshToken() {
                        getLocalLanguages(serviceId, viewModelCallback)
                    }
                })
        }
    }

    fun setLocaleChange(locale: String, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val languageRequest = NSLanguageLocaleRequest(locale)
            apiManager.setLocaleChange(
                languageRequest,
                object : NSRetrofitCallback<NSErrorResponse>(
                    viewModelCallback, NSApiErrorHandler.ERROR_LOCAL_CHANGE_LANGUAGE
                ) {
                    override fun <T> onResponse(response: Response<T>) {
                        CoroutineScope(Dispatchers.Main).launch {
                            viewModelCallback.onSuccess(response.body())
                        }
                    }

                    override fun onRefreshToken() {
                        getLocalLanguages(locale, viewModelCallback)
                    }
                })
        }
    }
}
package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSLanguageRequest
import com.nyotek.dot.admin.repository.network.requests.NSLanguageStringRequest
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallets
 */
object NSLanguageRepository: BaseRepository() {

    fun localLanguages(serviceId: String, viewModelCallback: NSGenericViewModelCallback) {
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
                        localLanguages(serviceId, viewModelCallback)
                    }
                })
        }
    }

    /**
     * To get languageString data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
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
}
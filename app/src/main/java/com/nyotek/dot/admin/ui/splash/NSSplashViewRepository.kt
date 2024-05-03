package com.nyotek.dot.admin.ui.splash

import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.repository.BaseRepository
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.manager.NSApiManager
import com.nyotek.dot.admin.repository.network.requests.NSAppThemeRequest
import com.nyotek.dot.admin.repository.network.requests.NSLanguageRequest
import com.nyotek.dot.admin.repository.network.requests.NSLanguageStringRequest
import com.nyotek.dot.admin.repository.network.responses.NSGetThemeModel
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse
import com.nyotek.dot.admin.repository.network.responses.SplashResponseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Repository class to handle data operations related to Splash Screen
 */
object NSSplashViewRepository : BaseRepository() {

    private var themeModel: NSGetThemeModel? = null

    fun getAppTheme(appId: String = BuildConfig.THEME_APP_ID, viewModelCallback: NSGenericViewModelCallback) {
        if (themeModel != null) {
            getAllDetailsAndProcess(themeModel?.data?.serviceId?:"", themeModel?:NSGetThemeModel(), viewModelCallback)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val nsAppThemeRequest = NSAppThemeRequest(appId)
                apiManager.getAppTheme(nsAppThemeRequest, object :
                    NSRetrofitCallback<NSGetThemeModel>(
                        viewModelCallback,
                        NSApiErrorHandler.ERROR_APP_THEME
                    ) {
                    override fun <T> onResponse(response: Response<T>) {
                        CoroutineScope(Dispatchers.Main).launch {
                            themeModel = response.body() as NSGetThemeModel
                            if (themeModel?.data?.serviceId.isNullOrEmpty()) {
                                viewModelCallback.onSuccess(SplashResponseModel(themeModel))
                            } else {
                                getAllDetailsAndProcess(themeModel?.data?.serviceId?:"", themeModel, viewModelCallback)
                            }
                        }
                    }

                    override fun onRefreshToken() {
                        getAppTheme(appId, viewModelCallback)
                    }
                })
            }
        }
    }

    fun getAllDetailsAndProcess(serviceId: String, themeModel: NSGetThemeModel?, viewModelCallback: NSGenericViewModelCallback) {
        CoroutineScope(Dispatchers.Main).launch {
            val localLanguage = getAllDetails(serviceId, viewModelCallback)

            // Process responses
            viewModelCallback.onSuccess(SplashResponseModel(themeModel, localLanguage))
        }
    }

    private suspend fun getAllDetails(
        serviceId: String, viewModelCallback: NSGenericViewModelCallback
    ): NSLocalLanguageResponse? {

        val localLanguageResponse = CoroutineScope(Dispatchers.IO).async {
            apiManager.getLocalLanguageAsync(serviceId, viewModelCallback)
        }

        val driverResponse = localLanguageResponse.await()

        return driverResponse
    }

    suspend fun getLocalLanguageStrings(serviceId: String, selectedLanguage: String = NSLanguageConfig.getSelectedLanguage(), viewModelCallback: NSGenericViewModelCallback) {
        val vendorResponseDeferred = CoroutineScope(Dispatchers.IO).async {
            apiManager.getLanguageListAsync(serviceId, selectedLanguage, viewModelCallback)
        }
        val vendorResponse = vendorResponseDeferred.await()
        viewModelCallback.onSuccess(vendorResponse)
    }

    suspend fun NSApiManager.getLocalLanguageAsync(
        serviceId: String,
        viewModelCallback: NSGenericViewModelCallback
    ): NSLocalLanguageResponse? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (serviceId.isEmpty()) {
                    continuation.resume(NSLocalLanguageResponse())
                } else {
                    listLocalLanguages(
                        NSLanguageRequest(serviceId),
                        object : NSRetrofitCallback<NSLocalLanguageResponse>(
                            viewModelCallback,
                            NSApiErrorHandler.ERROR_LOCAL_LANGUAGE
                        ) {

                            override fun <T> onResponse(response: Response<T>) {
                                if (response.body() is NSLocalLanguageResponse) {
                                    continuation.resume(response.body() as NSLocalLanguageResponse)
                                }
                            }

                            override fun <T> onErrorResponse(response: Response<T>) {
                                super.onErrorResponse(response)
                                continuation.resume(NSLocalLanguageResponse())
                            }

                            override fun onRefreshToken() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getLocalLanguageAsync(serviceId, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }

    suspend fun NSApiManager.getLanguageListAsync(
        serviceId: String, selectedLanguage: String = NSLanguageConfig.getSelectedLanguage(),
        viewModelCallback: NSGenericViewModelCallback
    ): NSLanguageStringResponse? {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (serviceId.isEmpty() || selectedLanguage.isEmpty()) {
                    continuation.resume(NSLanguageStringResponse())
                } else {
                    getLanguageString(
                        NSLanguageStringRequest(serviceId, selectedLanguage, null),
                        object : NSRetrofitCallback<NSLanguageStringResponse>(
                            viewModelCallback,
                            NSApiErrorHandler.ERROR_GET_LANGUAGE_STRING
                        ) {

                            override fun <T> onResponse(response: Response<T>) {
                                if (response.body() is NSLanguageStringResponse) {
                                    continuation.resume(response.body() as NSLanguageStringResponse)
                                }
                            }

                            override fun <T> onErrorResponse(response: Response<T>) {
                                super.onErrorResponse(response)
                                continuation.resume(NSLanguageStringResponse())
                            }

                            override fun onRefreshToken() {
                                CoroutineScope(Dispatchers.Main).launch {
                                    getLanguageListAsync(serviceId, selectedLanguage, viewModelCallback)
                                }
                            }
                        })
                }
            }
        }
    }
}
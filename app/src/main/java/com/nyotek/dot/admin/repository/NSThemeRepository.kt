package com.nyotek.dot.admin.repository

import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.repository.network.callbacks.NSGenericViewModelCallback
import com.nyotek.dot.admin.repository.network.callbacks.NSRetrofitCallback
import com.nyotek.dot.admin.repository.network.error.NSApiErrorHandler
import com.nyotek.dot.admin.repository.network.requests.NSAppThemeRequest
import com.nyotek.dot.admin.repository.network.responses.NSGetThemeModel
import com.nyotek.dot.admin.repository.network.responses.NSUploadFileResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallets
 */
object NSThemeRepository: BaseRepository() {

    /**
     * To get wallet data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getAppTheme(appId: String = BuildConfig.THEME_APP_ID,
                    viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val nsAppThemeRequest = NSAppThemeRequest(appId)
            apiManager.getAppTheme(nsAppThemeRequest, object :
                NSRetrofitCallback<NSGetThemeModel>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_APP_THEME
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body())
                    }
                }

                override fun onRefreshToken() {
                    getAppTheme(appId, viewModelCallback)
                }
            })
        }
    }

    /**
     * To upload image file API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun uploadFile(file: MultipartBody.Part,
                   viewModelCallback: NSGenericViewModelCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            apiManager.uploadFileData(file, object :
                NSRetrofitCallback<NSUploadFileResponse>(
                    viewModelCallback,
                    NSApiErrorHandler.ERROR_UPLOAD_FILE
                ) {
                override fun <T> onResponse(response: Response<T>) {
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModelCallback.onSuccess(response.body()?:NSUploadFileResponse())
                    }
                }

                override fun onRefreshToken() {
                    uploadFile(file, viewModelCallback)
                }
            })
        }
    }
}
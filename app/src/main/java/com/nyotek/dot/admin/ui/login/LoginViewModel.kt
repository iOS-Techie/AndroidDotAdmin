package com.nyotek.dot.admin.ui.login

import android.app.Activity
import android.app.Application
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.apiRefresh.NyoTokenRefresher
import com.nyotek.dot.admin.common.extension.getCompareAndGetDeviceLanguage
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSLanguageStringRequest
import com.nyotek.dot.admin.models.requests.NSLoginRequest
import com.nyotek.dot.admin.models.responses.NSGetThemeData
import com.nyotek.dot.admin.models.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.models.responses.NSMainDetailUser
import com.nyotek.dot.admin.models.responses.NSUserDetailResponse
import com.nyotek.dot.admin.models.responses.NSUserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    val languageConfig: NSLanguageConfig,
    val themeHelper: NSThemeHelper,
    colorResources: ColorResources,
    application: Application
) : BaseViewModel(repository, languageConfig.dataStorePreference, colorResources, application) {

    private suspend fun checkAllFieldsValid(email: String?, password: String?, callback: suspend (String, String, Boolean) -> Unit) {
        colorResources.apply {
            var errorId: String? = null
            when {
                email.isNullOrBlank() -> {
                    errorId = getStringResource().invalidEmailTitle
                }

                password.isNullOrBlank() -> {
                    errorId = getStringResource().invalidPasswordTitle
                }
            }
            if (errorId.isNullOrEmpty()) {
                callback.invoke(email?:"", password?:"", true)
            } else {
                callback.invoke(email?:"", password?:"", false)
                showError(errorId)
            }
        }
    }

    suspend fun login(activity: Activity, email: String, password: String, callback: (Boolean, Boolean) -> Unit) {
        showProgress()
        checkAllFieldsValid(email, password) { strEmail, strPassword, isSuccessValidation ->
            if (isSuccessValidation) {
                performApiCalls(
                    { repository.remote.loginWithEmailPassword(NSLoginRequest(strEmail, strPassword)) }
                ) { responses, isSuccess ->
                    if (isSuccess) {
                        val userResponse = responses[0] as NSUserResponse?
                        languageConfig.dataStorePreference.apply {
                            userData = userResponse
                            authToken = userResponse?.data?.accessToken
                            refreshToken = userResponse?.data?.refreshToken
                        }
                        NyoTokenRefresher.validate(this)
                        getUserMainDetail(activity, callback)
                    } else {
                        hideProgress()
                    }
                }
            } else {
                hideProgress()
            }
        }
    }
    
    private fun getUserMainDetail(activity: Activity, callback: (Boolean, Boolean) -> Unit) = viewModelScope.launch {
        getUserMainDetailApi(activity, callback)
    }
    
    private suspend fun getUserMainDetailApi(activity: Activity, callback: (Boolean, Boolean) -> Unit) {
        performApiCalls(
            { repository.remote.userDetail() }
        ) {response, isSuccess ->
            if (isSuccess) {
                val data = response[0] as NSUserDetailResponse?
                if (data != null) {
                    colorResources.themeHelper.setUserDetail(data.data)
                    setLanguageData(activity, data.data, callback)
                }
            } else {
                callback.invoke(false, false)
            }
        }
    }
    
    private fun setLanguageData(activity: Activity, userDetail: NSMainDetailUser?, callback: (Boolean, Boolean) -> Unit) {
        languageConfig.checkLocalLanguageSelected(activity, true, userDetail, themeHelper.getBootStrapData()) {
            if (it) {
                callback.invoke(false, true)
            } else {
                callback.invoke(true, false)
            }
        }
    }
}
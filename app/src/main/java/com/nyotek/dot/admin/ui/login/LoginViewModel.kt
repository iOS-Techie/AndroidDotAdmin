package com.nyotek.dot.admin.ui.login

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSLanguageConfig
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

    suspend fun login(email: String, password: String, callback: (Boolean, Boolean, Boolean) -> Unit) {
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
                        getUserDetail(colorResources.themeHelper.getThemeModel(), callback)
                    } else {
                        hideProgress()
                    }
                }
            } else {
                hideProgress()
            }
        }
    }

    private fun getUserDetail(response: NSGetThemeData?, callback: (Boolean, Boolean, Boolean) -> Unit) {

        fun checkLanguage(data: NSMainDetailUser?) {
            val localResponse = colorResources.themeHelper.getLocalLanguageLists()
            colorResources.themeHelper.setUserDetail(data)
            val selectedLanguage = languageConfig.getSelectedLanguage()
            if (data?.locale.isNullOrEmpty() && selectedLanguage.isEmpty()) {
                //When First Time App Start
                val lang = getCompareAndGetDeviceLanguage(localResponse)
                languageConfig.setLocalLanguage(lang.locale!!.lowercase(), lang.direction!!.lowercase())
                getLanguageStringData(response, lang.locale!!, callback)
            } else if (data?.locale?.isNotEmpty() == true) {

                val languageSelectedModel = localResponse.find { it.locale == data.locale }
                if (languageSelectedModel != null) {
                    languageConfig.setLanguagesPref(
                        data.locale?.lowercase(),
                        languageSelectedModel.direction.equals("rtl")
                    )
                    getLanguageStringData(response, data.locale!!.lowercase(), callback)
                } else {
                    hideProgress()
                    callback.invoke(false, false, true)
                }

            } else if(!data?.locale.equals(selectedLanguage) && selectedLanguage.isNotEmpty() && data?.locale != null){
                //When Language change from another device on same account
                val languageSelectedModel = localResponse.find { it.locale == data.locale }
                if (languageSelectedModel != null) {
                    languageConfig.setLanguagesPref(data.locale?.lowercase(), languageSelectedModel.direction.equals("rtl"))
                    getLanguageStringData(response, data.locale!!.lowercase(), callback)
                } else {
                    hideProgress()
                    callback.invoke(false, false, true)
                }
            } else if (selectedLanguage.isEmpty()){
                //When Local is Not empty but not selected any language
                hideProgress()
                callback.invoke(false, false, true)
            } else {
                getLanguageStringData(response, selectedLanguage, callback)
            }
        }

        if (languageConfig.dataStorePreference.isUserLoggedIn) {
            getUserMainDetail { data ->
                if (data is NSUserDetailResponse) {
                    val isDataAvailable = data.data != null
                    if (isDataAvailable) {
                        checkLanguage(data.data)
                    } else {
                        hideProgress()
                    }
                } else {
                    hideProgress()
                }
            }
        } else {
            checkLanguage(NSMainDetailUser())
        }
    }

    private fun getUserMainDetail(callback: (NSUserDetailResponse?) -> Unit) = viewModelScope.launch {
        getUserMainDetailApi(callback)
    }

    private suspend fun getUserMainDetailApi(callback: (NSUserDetailResponse?) -> Unit) {
        performApiCalls(
            { repository.remote.userDetail() }
        ) {response, isSuccess ->
            if (isSuccess) {
                val data = response[0] as NSUserDetailResponse?
                if (data != null) {
                    colorResources.themeHelper.setUserDetail(data.data)
                }
                callback.invoke(data)
            } else {
                callback.invoke(NSUserDetailResponse())
            }
        }
    }

    private fun getLanguageStringData(response: NSGetThemeData?, selectedLanguage: String = languageConfig.getSelectedLanguage(), callback: ((Boolean, Boolean, Boolean) -> Unit)) = viewModelScope.launch {
        getLanguageStringDataApi(response, selectedLanguage, callback)
    }

    private suspend fun getLanguageStringDataApi(response: NSGetThemeData?, selectedLanguage: String = languageConfig.getSelectedLanguage(), callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        performApiCalls(
            {repository.remote.getLanguageString(NSLanguageStringRequest(response?.serviceId, selectedLanguage)) }
        ) { responses, isSuccess ->
            if (isSuccess) {
                val languageStr = responses[0] as NSLanguageStringResponse?
                setLanguageJsonData(languageStr, callback)
            } else {
                callback.invoke(false, true, false)
            }
        }
    }

    fun setLanguageJsonData(data: NSLanguageStringResponse?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        val map: HashMap<String, String> = hashMapOf()
        for (languageStr in data?.data?: arrayListOf()) {
            if (languageStr.key?.isNotEmpty() == true) {
                map[languageStr.key] = languageStr.value ?: ""
            }
        }

        if (map.isNotEmpty()) {
            colorResources.themeHelper.setStringModel(map)
            callback.invoke(languageConfig.dataStorePreference.authToken?.isNotEmpty() == true, false, false)
        } else {
            callback.invoke(false, true, false)
        }
    }
}
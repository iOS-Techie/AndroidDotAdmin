package com.nyotek.dot.admin.ui.login

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSSingleLiveEvent
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.getCompareAndGetDeviceLanguage
import com.nyotek.dot.admin.repository.NSSocialRepository
import com.nyotek.dot.admin.repository.NSUserRepository
import com.nyotek.dot.admin.repository.network.responses.NSDetailUser
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserResponse
import com.nyotek.dot.admin.repository.network.responses.SplashResponseModel
import com.nyotek.dot.admin.ui.splash.NSSplashViewRepository
import kotlinx.coroutines.launch

/**
 * The view model class for login. It handles the business logic to communicate with the model for the login and provides the data to the observing UI component.
 */
class NSLoginViewModel(application: Application) : NSViewModel(application) {
    var strEmail: String? = null
    var strPassword: String? = null
    var isLoginSuccess = NSSingleLiveEvent<Boolean>()
    private val instance = NSApplication.getInstance()

    /**
     * To check all the mandatory fields are entered and valid
     *
     * @return status of all mandatory fields
     */
    private fun checkAllFieldsValid(): Boolean {
        var errorId: String? = null
        when {
            strEmail.isNullOrBlank() -> {
                errorId = stringResource.invalidEmailTitle
            }
            strPassword.isNullOrBlank() -> {
                errorId = stringResource.invalidPasswordTitle
            }
        }
        errorId?.let {
            validationErrorId.value = it
            return false
        }
        return true
    }

    /**
     * To initiate login process
     *
     */
    fun login(callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        if (checkAllFieldsValid()) {
            showProgress()
            callCommonApi({ obj ->
                NSUserRepository.loginWithEmailPassword(strEmail, strPassword, obj)
            }, { data, _ ->
                if (data is NSUserResponse) {
                    getUserDetail(callback)
                } else {
                    hideProgress()
                }
            })
        }
    }

    private fun getUserDetail(callback: ((Boolean, Boolean, Boolean) -> Unit)) {

        fun checkLanguage(data: NSDetailUser?) {
            val localResponse = instance.getLocalLanguages()
            NSUserManager.setUserDetail(data)
            val selectedLanguage = NSLanguageConfig.getSelectedLanguage()
            if (data?.locale.isNullOrEmpty() && selectedLanguage.isEmpty()) {
                //When First Time App Start
                val lang = getCompareAndGetDeviceLanguage(localResponse)
                NSLanguageConfig.setLocalLanguage(lang.locale!!.lowercase(), lang.direction!!.lowercase())
                getLocalStrings(NSConstants.SERVICE_ID, lang.locale!!, callback)
            }  else if (data?.locale?.isNotEmpty() == true) {

                val languageSelectedModel = localResponse.find { it.locale == data.locale }
                if (languageSelectedModel != null) {
                    NSLanguageConfig.setLanguagesPref(
                        data.locale?.lowercase(),
                        languageSelectedModel.direction.equals("rtl")
                    )
                    getLocalStrings(NSConstants.SERVICE_ID, data.locale!!.lowercase(), callback)
                } else {
                    hideProgress()
                    callback.invoke(false, false, true)
                }

            } else if(!data?.locale.equals(selectedLanguage) && selectedLanguage.isNotEmpty() && data?.locale != null){
                //When Language change from another device on same account
                val languageSelectedModel = localResponse.find { it.locale == data.locale }
                if (languageSelectedModel != null) {
                    NSLanguageConfig.setLanguagesPref(
                        data.locale?.lowercase(),
                        languageSelectedModel.direction.equals("rtl")
                    )
                    getLocalStrings(NSConstants.SERVICE_ID, data.locale!!.lowercase(), callback)
                } else {
                    hideProgress()
                    callback.invoke(false, false, true)
                }
            } else if (selectedLanguage.isEmpty()){
                //When Local is Not empty but not selected any language
                hideProgress()
                callback.invoke(false, false, true)
            } else {
                getLocalStrings(NSConstants.SERVICE_ID, selectedLanguage, callback)
            }
        }

        if (NSUserManager.isUserLoggedIn) {
            callCommonApi({ obj ->
                NSSocialRepository.getUserDetail(obj)
            }, { data, _ ->
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
            })
        } else {
            checkLanguage(NSDetailUser())
        }
    }

    private fun getLocalStrings(serviceId: String, selectedLanguage: String = NSLanguageConfig.getSelectedLanguage(), callback: (Boolean, Boolean, Boolean) -> Unit) {
        callCommonApi({ obj ->
            viewModelScope.launch {
                NSSplashViewRepository.getLocalLanguageStrings(
                    serviceId,selectedLanguage,
                    viewModelCallback = obj
                )
            }
        }, { data, _ ->
            if (data is NSLanguageStringResponse) {
                setLanguageJsonData(data, callback)
            }
        })
    }

    fun setLanguageJsonData(data: NSLanguageStringResponse?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        val map: HashMap<String, String> = hashMapOf()
        for (languageStr in data?.data?: arrayListOf()) {
            if (languageStr.key?.isNotEmpty() == true) {
                map[languageStr.key] = languageStr.value ?: ""
            }
        }
        if (map.isNotEmpty()) {
            instance.setStringModel(map)
            callback.invoke(true, false, false)
        } else {
            callback.invoke(false, true, false)
        }
    }

    override fun apiResponse(data: Any) {

    }
}
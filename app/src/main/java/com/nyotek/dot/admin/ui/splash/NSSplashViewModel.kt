package com.nyotek.dot.admin.ui.splash

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSUserManager
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.getCompareAndGetDeviceLanguage
import com.nyotek.dot.admin.repository.NSLanguageStringRepository
import com.nyotek.dot.admin.repository.NSSocialRepository
import com.nyotek.dot.admin.repository.network.responses.NSDetailUser
import com.nyotek.dot.admin.repository.network.responses.NSGetThemeData
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.repository.network.responses.NSUserDetailResponse
import com.nyotek.dot.admin.repository.network.responses.SplashResponseModel
import kotlinx.coroutines.launch


/**
 * The view model class for notification. It handles the business logic to communicate with the model for the notification and provides the data to the observing UI component.
 */
class NSSplashViewModel(application: Application) : NSViewModel(application) {

    private val gson = Gson()
    private val instance = NSApplication.getInstance()

    fun getAppThemeAndChangeLocaleFromSelection(isShowProgress: Boolean, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        if (NSUserManager.isUserLoggedIn) {
            if (NSConstants.isLanguageChange) {
                NSConstants.isLanguageChange = false
                if (isShowProgress) showProgress()
                callCommonApi({ obj ->
                    NSLanguageStringRepository.setLocaleChange(
                        NSLanguageConfig.getSelectedLanguage(),
                        viewModelCallback = obj
                    )
                }, { _, _ ->
                    getAllAppTheme(isShowProgress, callback)
                })
            } else {
                getAllAppTheme(isShowProgress, callback)
            }
        } else {
            getAllAppTheme(isShowProgress, callback)
        }
    }

    private fun getAllAppTheme(isShowProgress: Boolean, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            NSSplashViewRepository.getAppTheme(viewModelCallback = obj)
        }, { data, _ ->
            if (data is SplashResponseModel) {
                setThemeDetail(data, callback)
            }
        })
    }

    private fun setThemeDetail(splashResponse: SplashResponseModel?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        val themeModel = splashResponse?.themeModel
        if (themeModel != null) {
            themeModel.apply {
                NSConstants.THEME_ID = data?.themeId?:""
                NSConstants.SERVICE_ID = data?.serviceId?:""
                instance.setThemeModel(data?: NSGetThemeData())
                setLocalLanguage(splashResponse, callback)
            }
        } else {
            hideProgress()
            showError(stringResource.somethingWentWrong)
            callback.invoke(false, false, false)
        }
    }

    private fun setLocalLanguage(splashResponse: SplashResponseModel?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        instance.setLocalLanguage(splashResponse?.localResponse?.data?: arrayListOf())
        getUserDetail(splashResponse, callback)
    }

    fun getUserDetail(splashResponse: SplashResponseModel?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {

        fun checkLanguage(data: NSDetailUser?) {
            NSUserManager.setUserDetail(data)
            val selectedLanguage = NSLanguageConfig.getSelectedLanguage()
            if (data?.locale.isNullOrEmpty() && selectedLanguage.isEmpty()) {
                //When First Time App Start
                val lang = getCompareAndGetDeviceLanguage(splashResponse?.localResponse?.data?: arrayListOf())
                NSLanguageConfig.setLocalLanguage(lang.locale!!.lowercase(), lang.direction!!.lowercase())
                getLocalStrings(NSConstants.SERVICE_ID, lang.locale!!, callback)
            } else if(!data?.locale.equals(selectedLanguage) && selectedLanguage.isNotEmpty() && data?.locale != null){
                //When Language change from another device on same account
                val languageSelectedModel = splashResponse?.localResponse?.data?.find { it.locale == data.locale }
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
            // val languageString = Gson().toJson(map)
            // val stringResource = Gson().fromJson(languageString, StringResourceResponse::class.java)
            instance.setStringModel(map)
            callback.invoke(true, false, false)
        } else {
            callback.invoke(false, true, false)
        }
    }

    override fun apiResponse(data: Any) {

    }
}
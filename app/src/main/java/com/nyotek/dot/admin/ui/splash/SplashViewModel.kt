package com.nyotek.dot.admin.ui.splash

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewModel
import com.nyotek.dot.admin.common.NSDataStorePreferences
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.NSThemeHelper
import com.nyotek.dot.admin.common.extension.getCompareAndGetDeviceLanguage
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.data.Repository
import com.nyotek.dot.admin.models.requests.NSAppThemeRequest
import com.nyotek.dot.admin.models.requests.NSLanguageLocaleRequest
import com.nyotek.dot.admin.models.requests.NSLanguageRequest
import com.nyotek.dot.admin.models.requests.NSLanguageStringRequest
import com.nyotek.dot.admin.models.responses.NSGetThemeData
import com.nyotek.dot.admin.models.responses.NSGetThemeModel
import com.nyotek.dot.admin.models.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.models.responses.NSLocalLanguageResponse
import com.nyotek.dot.admin.models.responses.NSMainDetailUser
import com.nyotek.dot.admin.models.responses.NSUserDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: Repository,
    val dataStoreRepository: NSDataStorePreferences,
    private val themeHelper: NSThemeHelper,
    val languageConfig: NSLanguageConfig,
    application: Application
) : BaseViewModel(repository, dataStoreRepository, ColorResources(themeHelper), application) {

    var allDataLoaded: MutableLiveData<Boolean> = MutableLiveData()

    fun getAppThemeAndChangeLocaleFromSelection(context: Context, callback: ((Boolean, Boolean, Boolean) -> Unit)) = viewModelScope.launch {
        getAppThemeAndChangeLocaleFromSelectionApi(context, callback)
    }

    private suspend fun getAppThemeAndChangeLocaleFromSelectionApi(context: Context, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        if (dataStoreRepository.isUserLoggedIn) {
            if (NSThemeHelper.isLanguageChange) {
                NSThemeHelper.isLanguageChange = false
                performApiCalls(
                    { repository.remote.setLocal(NSLanguageLocaleRequest(languageConfig.getSelectedLanguage())) }
                ) { _, _ ->
                    getAppThemeStart(context, callback)
                }
            } else {
                getAppThemeStart(context, callback)
            }
        } else {
            getAppThemeStart(context, callback)
        }
    }

    fun getAppThemeStart(context: Context, callback: ((Boolean, Boolean, Boolean) -> Unit)) = viewModelScope.launch {
        getAppTheme(context, callback)
    }

    private suspend fun getAppTheme(context: Context, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        //showProgress()
        performApiCalls(
            { repository.remote.getAppTheme(NSAppThemeRequest(BuildConfig.THEME_APP_ID)) }
        ) { responses, isSuccess ->
            if (isSuccess) {
                setThemeDetail(context, responses[0] as NSGetThemeModel?, callback)
            } else {
                hideProgress()
                val something = context.resources.getString(R.string.something_went_wrong)
                showError(something)
                callback.invoke(false, false, false)
            }
        }
    }

    private fun setThemeDetail(context: Context, themeModel: NSGetThemeModel?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        if (themeModel != null) {
            themeModel.apply {
                themeHelper.THEME_ID = data?.themeId?:""
                themeHelper.SERVICE_ID = data?.serviceId?:""
                themeHelper.setThemeModel(data?: NSGetThemeData())
                viewModelScope.launch {
                    getAppThemeAllData(themeModel.data, callback)
                }
            }
        } else {
            hideProgress()
            val something = context.resources.getString(R.string.something_went_wrong)
            showError(something)
            callback.invoke(false, false, false)
        }
    }

    private suspend fun getAppThemeAllData(response: NSGetThemeData?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        performApiCalls(
            { repository.remote.listLocalLanguage(NSLanguageRequest(response?.serviceId))}
        ) { responses, isSuccess ->
            if (isSuccess) {
                val localList = responses[0] as NSLocalLanguageResponse?
                setLocalLanguage(localList, response, callback)
            } else {
                hideProgress()
                callback.invoke(false, false, false)
            }
        }

    }

    private fun setLocalLanguage(localLanguage: NSLocalLanguageResponse?, response: NSGetThemeData?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {
        themeHelper.setLocalLanguageList(localLanguage?.data?: arrayListOf())
        //languageConfig.setDefaultLanguage()
        getUserDetail(localLanguage, response, callback)
    }

    private fun getUserDetail(localResponse: NSLocalLanguageResponse?, response: NSGetThemeData?, callback: ((Boolean, Boolean, Boolean) -> Unit)) {

        fun checkLanguage(data: NSMainDetailUser?) {
            colorResources.themeHelper.setUserDetail(data)
            val selectedLanguage = languageConfig.getSelectedLanguage()
            if (data?.locale.isNullOrEmpty() && selectedLanguage.isEmpty()) {
                //When First Time App Start
                val lang = getCompareAndGetDeviceLanguage(localResponse?.data?: arrayListOf())
                languageConfig.setLocalLanguage(lang.locale!!.lowercase(), lang.direction!!.lowercase())
                getLanguageStringData(response, lang.locale!!, callback)
            } else if (data?.locale?.isNotEmpty() == true) {

                val languageSelectedModel = localResponse?.data?.find { it.locale == data.locale }
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
                val languageSelectedModel = localResponse?.data?.find { it.locale == data.locale }
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

        if (dataStoreRepository.isUserLoggedIn) {
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
            themeHelper.setStringModel(map)
            callback.invoke(true, false, false)
        } else {
            callback.invoke(false, true, false)
        }
    }
}
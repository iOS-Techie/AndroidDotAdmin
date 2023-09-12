package com.nyotek.dot.admin.ui.splash

import android.app.Application
import com.google.gson.Gson
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSConstants
import com.nyotek.dot.admin.common.NSViewModel
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.repository.NSLanguageRepository
import com.nyotek.dot.admin.repository.NSThemeRepository
import com.nyotek.dot.admin.repository.network.requests.NSLanguageStringRequest
import com.nyotek.dot.admin.repository.network.responses.NSGetThemeModel
import com.nyotek.dot.admin.repository.network.responses.NSLanguageStringResponse
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse


/**
 * The view model class for notification. It handles the business logic to communicate with the model for the notification and provides the data to the observing UI component.
 */
class NSSplashViewModel(application: Application) : NSViewModel(application) {

    fun getAppTheme(callback: ((Boolean) -> Unit)) {
        callCommonApi({ obj ->
            NSThemeRepository.getAppTheme(viewModelCallback = obj)
        }, { data, _ ->
            if (data is NSGetThemeModel) {
                //First Get Theme Api and that response using called VendorByService Api and get Response
                val themeModel = data as NSGetThemeModel?
                if (themeModel != null) {
                    setThemeData(themeModel)
                    getLocalLanguage(callback)
                } else {
                    showError(stringResource.somethingWentWrong)
                    callback.invoke(false)
                }
            }
        })
    }

    private fun setThemeData(themeModel: NSGetThemeModel) {
        NSConstants.THEME_ID = themeModel.data?.themeId!!
        NSConstants.SERVICE_ID = themeModel.data.serviceId!!
        NSApplication.getInstance().setThemeModel(themeModel.data)
    }

    private fun getLocalLanguage(callback: ((Boolean) -> Unit)) {
        callCommonApi({ obj ->
            NSLanguageRepository.localLanguages(NSConstants.SERVICE_ID, obj)
        }, { data, _ ->
            if (data is NSLocalLanguageResponse) {
                NSApplication.getInstance().setLocalLanguage(data.data)
                NSLanguageConfig.setDefaultLanguage()
                getLanguageList(callback)
            } else {
                callback.invoke(false)
            }
        })
    }

    private fun getLanguageList(callback: ((Boolean) -> Unit)) {
        val languageStringRequest = NSLanguageStringRequest(NSConstants.SERVICE_ID, NSLanguageConfig.getSelectedLanguage(), null)

        callCommonApi({ obj ->
            NSLanguageRepository.getLanguageString(languageStringRequest,obj)
        }, { data, _ ->
            if (data is NSLanguageStringResponse) {
                setLanguageString(data)
                callback.invoke(true)
            } else {
                callback.invoke(false)
            }
        })
    }

    private fun setLanguageString(data: NSLanguageStringResponse) {
        val map: HashMap<String, String> = hashMapOf()
        for (languageStr in data.data) {
            if (languageStr.key?.isNotEmpty() == true) {
                map[languageStr.key] = languageStr.value?:""
            }
        }
        val languageString = Gson().toJson(map)
        val stringResource = Gson().fromJson(languageString, StringResourceResponse::class.java)
        NSApplication.getInstance().setStringModel(map)
        NSApplication.getInstance().setStringMap(map)
    }

    override fun apiResponse(data: Any) {

    }
}
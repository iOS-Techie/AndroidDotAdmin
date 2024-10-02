package com.nyotek.dot.admin.common

import android.app.Activity
import android.content.Context
import com.franmontiel.localechanger.LocaleChanger
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.apiRefresh.NyoTokenRefresher
import com.nyotek.dot.admin.common.extension.getCompareAndGetDeviceLanguage
import com.nyotek.dot.admin.common.extension.getLanguageCode
import com.nyotek.dot.admin.common.extension.getLanguageRegion
import com.nyotek.dot.admin.common.extension.getLocalLanguage
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.models.responses.BootStrapData
import com.nyotek.dot.admin.models.responses.NSMainDetailUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


/**
 * The language class that handles tasks that are common throughout the application languages
 */
@Singleton
class NSLanguageConfig @Inject constructor(val dataStorePreference: NSDataStorePreferences, val themeHelper: NSThemeHelper) {

    fun init(context: Context) {
        LocaleChanger.initialize(context, getLanguageList())
        if (dataStorePreference.isLanguageSelected == false) {
            LocaleChanger.resetLocale()
        }
    }

    private fun getLanguageList(): MutableList<Locale> {
        val languageList: MutableList<Locale> = arrayListOf()
        for (data in Locale.getAvailableLocales()) {
            val local = Locale(data.language.lowercase(), data.country, data.variant)
            languageList.add(local)
        }
        return languageList
    }

    fun setDefaultLanguage() {
        CoroutineScope(Dispatchers.IO).launch {
            val languageData: String? = dataStorePreference.languageData
            val isLanguageSelected: Boolean? = dataStorePreference.isLanguageSelected

            val languageList = themeHelper.getLocalLanguageLists()
            var position = 0
            for (language in languageList) {
                if (language.locale != null) {
                    if (language.locale!!.lowercase().contains(getLocalLanguage().lowercase()) && (languageData != null && languageData.isEmpty())) {
                        setLanguagesPref(language.locale, language.direction.equals("rtl"))
                        break
                    } else if (language.locale!!.lowercase().contains(getLocalLanguage().lowercase()) && isLanguageSelected == false) {
                        setLanguagesPref(language.locale, language.direction.equals("rtl"))
                        break
                    }
                    position++
                }
            }
        }
    }

    fun setLanguagesPref(languageName: String?, isRtl: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStorePreference.languageData = languageName
            dataStorePreference.isLanguageRTL = isRtl
            CoroutineScope(Dispatchers.Main).launch {
                setLanguage()
            }
        }
    }

    fun setLocalLanguage(language: String, direction: String) {
        dataStorePreference.isLanguageRTL = direction == "rtl"
        LocaleChanger.setLocale(Locale(getLanguageCode(language), getLanguageRegion(language)))
    }

    private fun setLanguage() {
        val languageData = dataStorePreference.languageData
        LocaleChanger.setLocale(Locale(getLanguageCode(languageData?:""), getLanguageRegion(languageData?:"")))
    }

    fun logout() {
        NyoTokenRefresher.forceStop()
        //NSApplication.getInstance().getApiManager().cancelAllRequests()
        val language = dataStorePreference.languageData
        val languageSelected = dataStorePreference.isLanguageSelected
        val isDirection = dataStorePreference.isLanguageRTL
        dataStorePreference.clearPrefData()
        dataStorePreference.languageData = language
        dataStorePreference.isLanguageRTL = isDirection
        dataStorePreference.isLanguageSelected = languageSelected
    }

    fun getSelectedLanguage(): String {
        val languageData = dataStorePreference.languageData
        return (languageData?:"").lowercase()
    }

    fun isLanguageRtl(): Boolean {
        return dataStorePreference.isLanguageRTL
    }
    
    fun checkLocalLanguageSelected(
        activity: Activity, isClear: Boolean,
        userDetail: NSMainDetailUser?,
        bootStrapData: BootStrapData?,
        callback: ((Boolean) -> Unit)
    ) {
        val strings = bootStrapData?.strings
        val selectedLanguage = getSelectedLanguage()
        if (isClear && userDetail?.locale.isNullOrEmpty()) {
            clearLanguage()
        }
        if (userDetail?.locale.isNullOrEmpty() && selectedLanguage.isEmpty()) {
            //When First Time App Start
            checkUserLocalAndSelectedLngEmpty(activity, userDetail, bootStrapData, callback)
        } else if (userDetail?.locale?.isNotEmpty() == true) {
            checkUserLocalNotEmpty(activity, userDetail, bootStrapData, callback)
        } else if (!userDetail?.locale.equals(selectedLanguage) && selectedLanguage.isNotEmpty() && userDetail?.locale != null) {
            //When Language change from another device on same account
            compareSelectedLanguage(activity, userDetail, bootStrapData, callback)
        } else if (selectedLanguage.isEmpty()) {
            //When Local is Not empty but not selected any language
            callback.invoke(true)
        } else {
            createLanguageMap(activity, strings, selectedLanguage) {
                callback.invoke(false)
            }
        }
    }
    
    private fun checkUserLocalAndSelectedLngEmpty(
        activity: Activity,
        userDetail: NSMainDetailUser?,
        bootStrapData: BootStrapData?,
        callback: (Boolean) -> Unit
    ) {
        val locales = bootStrapData?.locales
        val strings = bootStrapData?.strings
        
        val lang = getCompareAndGetDeviceLanguage(locales ?: arrayListOf())
        setLocalLanguage(lang.locale!!.lowercase(), lang.direction!!.lowercase())
        if (!userDetail?.id.isNullOrEmpty() && userDetail?.locale.isNullOrEmpty()) {
            callback.invoke(true)
        } else {
            createLanguageMap(activity, strings, lang.locale!!) {
                callback.invoke(false)
            }
        }
    }
    
    private fun checkUserLocalNotEmpty(
        activity: Activity,
        userDetail: NSMainDetailUser?,
        bootStrapData: BootStrapData?,
        callback: (Boolean) -> Unit
    ) {
        val locales = bootStrapData?.locales
        val strings = bootStrapData?.strings
        
        val languageSelectedModel =
            locales?.find { it.locale == userDetail?.locale }
        if (languageSelectedModel != null) {
            setLanguagesPref(
                userDetail?.locale?.lowercase(),
                languageSelectedModel.direction.equals("rtl")
            )
            createLanguageMap(activity, strings, userDetail?.locale!!.lowercase()) {
                callback.invoke(false)
            }
        } else {
            callback.invoke(true)
        }
    }
    
    private fun compareSelectedLanguage(
        activity: Activity,
        userDetail: NSMainDetailUser?,
        bootStrapData: BootStrapData?,
        callback: (Boolean) -> Unit
    ) {
        val locales = bootStrapData?.locales
        val strings = bootStrapData?.strings
        
        val languageSelectedModel = locales?.find { it.locale == userDetail?.locale }
        if (languageSelectedModel != null) {
            setLanguagesPref(
                userDetail?.locale?.lowercase(),
                languageSelectedModel.direction.equals("rtl")
            )
            createLanguageMap(activity, strings, userDetail?.locale!!.lowercase()) {
                callback.invoke(false)
            }
        } else {
            callback.invoke(true)
        }
    }
    
    fun createLanguageMap(
        activity: Activity,
        strings: HashMap<String, HashMap<String, String>>?,
        languageCode: String,
        callback: () -> Unit
    ) {
        val map = HashMap<String, String>()
        
        for ((key, translations) in strings ?: hashMapOf()) {
            translations[languageCode]?.let {
                map[key] = it
            }
        }
        
        if (map.isNotEmpty()) {
            setStringModel(map, callback)
        } else {
            getLocalLngFromJsonFile(activity) {
                createLanguageMap(activity, it, languageCode, callback)
            }
        }
    }
    
    private fun setStringModel(map: HashMap<String, String>, callback: () -> Unit) {
        themeHelper.setStringModel(map)
        if (!themeHelper.getThemeModel().backgroundImages.isValidList()) {
            callback.invoke()
        } else {
            callback.invoke()
        }
    }
    
    private fun getLocalLngFromJsonFile(
        activity: Activity,
        callback: ((HashMap<String, HashMap<String, String>>) -> Unit),
    ) {
        NSUtilities.getLocalJsonRowDataNew(activity, R.raw.local_json) {
            callback.invoke(it)
        }
    }
    
    fun clearLanguage() {
        dataStorePreference.languageData = ""
        dataStorePreference.languagePosition = -1
    }
}
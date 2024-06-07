package com.nyotek.dot.admin.common

import android.content.Context
import com.franmontiel.localechanger.LocaleChanger
import com.nyotek.dot.admin.common.extension.getLanguageCode
import com.nyotek.dot.admin.common.extension.getLanguageRegion
import com.nyotek.dot.admin.common.extension.getLocalLanguage
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
}
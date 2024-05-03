package com.nyotek.dot.admin.common.utils

import android.content.Context
import com.franmontiel.localechanger.LocaleChanger
import com.nyotek.dot.admin.common.apiRefresh.NyoTokenRefresher
import com.nyotek.dot.admin.common.NSApplication
import java.util.Locale


/**
 * The language class that handles tasks that are common throughout the application languages
 */
object NSLanguageConfig {

    fun init(context: Context) {
        LocaleChanger.initialize(context, getLanguageList())
        if (NSApplication.getInstance().getPrefs().isLanguageSelected == false) {
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
        val pref = NSApplication.getInstance().getPrefs()
        val languageList = NSApplication.getInstance().getLocalLanguages()
        var position = 0
        for (language in languageList) {
            if (language.locale != null) {
                if (language.locale!!.lowercase().contains(getLocalLanguage().lowercase()) && (pref.languageData != null && pref.languageData!!.isEmpty())) {
                    setLanguagesPref(language.locale, language.direction.equals("rtl"))
                    break
                } else if (language.locale!!.lowercase().contains(getLocalLanguage().lowercase()) && pref.isLanguageSelected == false) {
                    setLanguagesPref(language.locale, language.direction.equals("rtl"))
                    break
                }
                position++
            }
        }
    }

    fun setLanguagesPref(languageName: String?, isRtl: Boolean) {
        val pref = NSApplication.getInstance().getPrefs()
        pref.languageData = languageName
        pref.isLanguageRTL = isRtl
        setLanguage()
    }

    fun setLocalLanguage(language: String, direction: String) {
        val pref = NSApplication.getInstance().getPrefs()
        pref.isLanguageRTL = direction == "rtl"
        LocaleChanger.setLocale(Locale(getLanguageCode(language), getLanguageRegion(language)))
    }


    private fun setLanguage() {
        val pref = NSApplication.getInstance().getPrefs()
        LocaleChanger.setLocale(Locale(getLanguageCode(pref.languageData!!), getLanguageRegion(pref.languageData!!)))
    }

    fun logout() {
        NyoTokenRefresher.forceStop()
        NSApplication.getInstance().getApiManager().cancelAllRequests()
        val pref = NSApplication.getInstance().getPrefs()
        val language = pref.languageData
        val languageSelected = pref.isLanguageSelected
        val isDirection = pref.isLanguageRTL
        NSApplication.getInstance().getPrefs().clearPrefData()
        pref.languageData = language
        pref.isLanguageSelected = languageSelected
        pref.isLanguageRTL = isDirection
    }

    fun getSelectedLanguage(): String {
        val pref = NSApplication.getInstance().getPrefs()
        return (pref.languageData?:"").lowercase()
    }

    fun isLanguageRtl(): Boolean {
        val pref = NSApplication.getInstance().getPrefs()
        return pref.isLanguageRTL
    }
}
package com.nyotek.dot.admin.common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.repository.network.responses.NSUserResponse

/**
 * Class to maintain shared preference
 */
class NSPreferences(context: Context) {
    private val preference: SharedPreferences =
        context.getSharedPreferences(BuildConfig.PREFERENCE, Context.MODE_PRIVATE)
    private val prefEdit: SharedPreferences.Editor = preference.edit()

    companion object {
        //Keys for user data
        private const val KEY_AUTH_TOKEN = "token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_LANGUAGE_POSITION = "key_language_position"
        private const val KEY_LANGUAGE_SELECT = "key_language_data"
        private const val KEY_USER_DATA = "key_user_data"
        private const val KEY_SELECTED_ADDRESS = "key_selected_address"
        private const val KEY_SELECTED_ADDRESS_DETAIL = "key_selected_address_detail"
        private const val KEY_LANGUAGE_SELECTED = "key_language_selected"
        private const val KEY_LANGUAGE_SELECT_RTL = "key_language_data_direction"
        private const val KEY_TEMP_LANGUAGE_POSITION = "key_temp_language_position"
    }

    /**
     * Property that contains the authentication token to used for authentication web service call
     */
    var authToken: String?
        get() = preference.getString(KEY_AUTH_TOKEN, null)
        set(token) = prefEdit.putString(KEY_AUTH_TOKEN, token).apply()

    /**
     * Property that contains the refresh token to used for web service call that is used to get new authentication token
     */
    var refreshToken: String?
        get() = preference.getString(KEY_REFRESH_TOKEN, null)
        set(token) = prefEdit.putString(KEY_REFRESH_TOKEN, token).apply()

    /**
     * Property that contains language selected data
     */
    var languagePosition: Int?
        get() {
            return preference.getInt(KEY_LANGUAGE_POSITION, -1)
        }
        set(language) {
            prefEdit.putInt(KEY_LANGUAGE_POSITION, language!!).apply()
        }

    /**
     * Property that contains language selected data
     */
    var languageData: String?
        get() {
            return preference.getString(KEY_LANGUAGE_SELECT, "")
        }
        set(language) {
            prefEdit.putString(KEY_LANGUAGE_SELECT, language).apply()
        }

    var isLanguageRTL: Boolean
        get() {
            return preference.getBoolean(KEY_LANGUAGE_SELECT_RTL, false)
        }
        set(language) {
            prefEdit.putBoolean(KEY_LANGUAGE_SELECT_RTL, language).apply()
        }

    var languageTempPosition: Int?
        get() {
            return preference.getInt(KEY_TEMP_LANGUAGE_POSITION, -1)
        }
        set(language) {
            prefEdit.putInt(KEY_TEMP_LANGUAGE_POSITION, language!!).apply()
        }

    /**
     * Property that contains language selected data
     */
    var isLanguageSelected: Boolean?
        get() {
            return preference.getBoolean(KEY_LANGUAGE_SELECTED, false)
        }
        set(language) {
            prefEdit.putBoolean(KEY_LANGUAGE_SELECTED, language!!).apply()
        }

    /**
     * Property that contains login user data
     */
    var userData: NSUserResponse?
        get() {
            val json: String? = preference.getString(KEY_USER_DATA, null)
            return Gson().fromJson(json, NSUserResponse::class.java)
        }
        set(loginResponse) {
            val json: String = Gson().toJson(loginResponse)
            prefEdit.putString(KEY_USER_DATA, json).apply()
        }

    /**
     * Property that contains selected address id
     */
    var selectedAddress: String?
        get() {
            return preference.getString(KEY_SELECTED_ADDRESS, "")
        }
        set(address) {
            prefEdit.putString(KEY_SELECTED_ADDRESS, address!!).apply()
        }

    /**
     * Property that contains selected address detail
     */
    var selectedAddressDetail: String?
        get() {
            return preference.getString(KEY_SELECTED_ADDRESS_DETAIL, "")
        }
        set(language) {
            prefEdit.putString(KEY_SELECTED_ADDRESS_DETAIL, language!!).apply()
        }

    /**
     * To clear all preferences data
     */
    fun clearPrefData() {
        prefEdit.clear().apply()
    }
}
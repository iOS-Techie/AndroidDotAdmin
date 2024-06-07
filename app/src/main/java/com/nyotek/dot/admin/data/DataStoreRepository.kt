package com.nyotek.dot.admin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.common.keys.PreferencesKey
import com.nyotek.dot.admin.models.responses.NSUserResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(BuildConfig.PREFERENCE)
@Singleton
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val authKey = stringPreferencesKey(PreferencesKey.KEY_AUTH_TOKEN)
        val refreshTokenKey = stringPreferencesKey(PreferencesKey.KEY_REFRESH_TOKEN)
        val languageSelectKey = stringPreferencesKey(PreferencesKey.KEY_LANGUAGE_SELECT)
        val userDataKey = stringPreferencesKey(PreferencesKey.KEY_USER_DATA)
        val selectAddressKey = stringPreferencesKey(PreferencesKey.KEY_SELECTED_ADDRESS)
        val selectAddressDetailKey = stringPreferencesKey(PreferencesKey.KEY_SELECTED_ADDRESS_DETAIL)
        val languagePositionKey = intPreferencesKey(PreferencesKey.KEY_LANGUAGE_POSITION)
        val languageSelectedKey = booleanPreferencesKey(PreferencesKey.KEY_LANGUAGE_SELECTED)
        val languageRtlKey = booleanPreferencesKey(PreferencesKey.KEY_LANGUAGE_SELECT_RTL)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore
    private val gson = Gson()

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.authKey] = token
        }
    }

    val readAuthToken: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val authToken = preferences[PreferenceKeys.authKey] ?: ""
            authToken
        }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.refreshTokenKey] = token
        }
    }

    val readRefreshToken: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val authToken = preferences[PreferenceKeys.refreshTokenKey] ?: ""
            authToken
        }

    suspend fun saveLanguageSelected(language: String?) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.languageSelectKey] = language?:""
        }
    }

    val readLanguageSelected: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val authToken = preferences[PreferenceKeys.languageSelectKey] ?: ""
            authToken
        }

    suspend fun saveSelectedAddress(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectAddressKey] = language
        }
    }

    val readSelectedAddress: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val authToken = preferences[PreferenceKeys.selectAddressKey] ?: ""
            authToken
        }

    suspend fun saveSelectedAddressDetail(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectAddressDetailKey] = language
        }
    }

    val readSelectedAddressDetail: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val authToken = preferences[PreferenceKeys.selectAddressDetailKey] ?: ""
            authToken
        }

    suspend fun saveLanguagePosition(position: Int) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.languagePositionKey] = position
        }
    }

    val readSelectedLanguagePosition: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val authToken = preferences[PreferenceKeys.languagePositionKey] ?: -1
            authToken
        }

    suspend fun saveIsLanguageSelected(isSelected: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.languageSelectedKey] = isSelected
        }
    }

    val readIsLanguageSelected: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val authToken = preferences[PreferenceKeys.languageSelectedKey] ?: false
            authToken
        }

    suspend fun saveIsLanguageRtl(isRtl: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.languageRtlKey] = isRtl
        }
    }

    val readIsLanguageRtl: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val authToken = preferences[PreferenceKeys.languageRtlKey] ?: false
            authToken
        }



    suspend fun saveUserData(
        userData: NSUserResponse?
    ) {
        dataStore.edit { preferences ->
            if (userData != null) {
                preferences[PreferenceKeys.userDataKey] = gson.toJson(userData)
            }
        }
    }

    suspend fun clearData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    val readUserData: Flow<NSUserResponse> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userData = preferences[PreferenceKeys.userDataKey] ?: ""
            if (userData.isEmpty()) {
                NSUserResponse()
            } else {
                gson.fromJson(userData, NSUserResponse::class.java)
            }
        }
}
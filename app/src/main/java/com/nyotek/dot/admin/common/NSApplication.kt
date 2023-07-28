package com.nyotek.dot.admin.common

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.franmontiel.localechanger.LocaleChanger
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.location.NSLocationManager
import com.nyotek.dot.admin.repository.network.manager.NSApiManager
import com.nyotek.dot.admin.repository.network.responses.CapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel
import com.nyotek.dot.admin.repository.network.responses.NSGetThemeData
import com.nyotek.dot.admin.repository.network.responses.ServiceCapabilitiesDataItem
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * The DoT application class containing Preference, network manager and functionality
 * to be used across modules
 */
class NSApplication : Application() {
    private lateinit var preferences: NSPreferences
    private lateinit var apiManager: NSApiManager
    private lateinit var locationManager: NSLocationManager
    private lateinit var permissionHelper: NSPermissionHelper
    private var selectedNavigationType: String? = null
    private lateinit var themeModel: NSGetThemeData
    private var apiFailCode: Int = 0
    private lateinit var stringResource: StringResourceResponse
    private var capabilityList: MutableList<CapabilitiesDataItem> = arrayListOf()
    private var fleetLanguageList: MutableList<LanguageSelectModel> = arrayListOf()
    private var localLanguageList: MutableList<LanguageSelectModel> = arrayListOf()
    private var localMapLanguageList: HashMap<String, MutableList<LanguageSelectModel>> = hashMapOf()
    private var capabilityItemList: HashMap<String, ServiceCapabilitiesDataItem> = hashMapOf()
    private var jobTitleMap: HashMap<String, JobListDataItem> = hashMapOf()

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initInstance()
        NSLanguageConfig.init(applicationContext)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleChanger.onConfigurationChanged()
    }

    /**
     * To initialize global instances that be used across modules
     */
    private fun initInstance() {
        instance = this
        preferences = NSPreferences(this)
        locationManager = NSLocationManager(this)
        permissionHelper = NSPermissionHelper(this)

        apiManager = NSApiManager()
        isAlertShown = false
    }

    fun setFleetLanguageList(list: MutableList<LanguageSelectModel>) {
        fleetLanguageList.clear()
        fleetLanguageList.addAll(list)
    }

    fun getFleetLanguageList(): MutableList<LanguageSelectModel> {
        return fleetLanguageList
    }

    fun getCapabilityItemList(): HashMap<String, ServiceCapabilitiesDataItem> = capabilityItemList

    fun setCapabilityItemList(filter: HashMap<String, ServiceCapabilitiesDataItem>) {
        capabilityItemList = filter
    }

    /**
     * To get instance of Job Role
     */
    fun getJobRolesTypes(): HashMap<String, JobListDataItem> = jobTitleMap

    fun setJobRoleType(filter: HashMap<String, JobListDataItem>) {
        jobTitleMap = filter
    }

    /**
     * To get instance of Resource Model
     */
    fun getCapabilityList(): MutableList<CapabilitiesDataItem> = capabilityList

    fun setCapabilityList(capabilities: MutableList<CapabilitiesDataItem>) {
        capabilityList = capabilities
    }

    /**
     * To get instance of String Resource Model
     */
    fun getStringModel(): StringResourceResponse = if (this::stringResource.isInitialized) {
        stringResource
    } else {
        StringResourceResponse()
    }

    fun setStringModel(resources: StringResourceResponse) {
        stringResource = resources
    }

    /**
     * To get the instance of Location manager
     *
     * @return The location manager instance
     */
    fun getLocationManager(): NSLocationManager = locationManager

    /**
     * To get instance of Resource Model
     */
    fun getThemeModel(): NSGetThemeData = themeModel

    fun setThemeModel(resources: NSGetThemeData) {
        themeModel = resources
    }

    /**
     * To get the instance of GeoCodeLocation Count
     *
     * @return The Geo list instance
     */
    fun increaseGeoCodeLocationCount(): Int {
        return geoCodeLocationCount++
    }

    /**
     * To get the instance of GeoCodeLocation Count
     *
     * @return The Geo list instance
     */
    fun getGeoCodeLocationCount(): Int {
        return geoCodeLocationCount
    }

    /**
     * To get instance of Resource Model
     */
    fun getApiFailCode(): Int = apiFailCode

    fun setApiFailCode(code: Int) {
        Log.d("UserDetailCall", "setApiFailCode: 7 $code")
        apiFailCode = code
    }

    /**
     * To get instances of shared preferences
     */
    fun getPrefs(): NSPreferences = preferences

    /**
     * To get instance of Api manager
     */
    fun getApiManager(): NSApiManager = apiManager

    /**
     * To get the instance of Permission helper
     *
     * @return The permission helper instance
     */
    fun getPermissionHelper(): NSPermissionHelper = permissionHelper

    /**
     * To get instance of selected Navigation type
     */
    fun getSelectedNavigationType(): String? = selectedNavigationType

    /**
     * To set instance of selected Navigation type
     */
    fun setSelectedNavigationType(type: String?) {
        selectedNavigationType = type
    }

    fun setLocalLanguage(list: MutableList<LanguageSelectModel>) {
        localLanguageList = list
    }

    fun getLocalLanguages(): MutableList<LanguageSelectModel> {
        return localLanguageList
    }

    fun setMapLocalLanguage(list: HashMap<String,MutableList<LanguageSelectModel>>) {
        localMapLanguageList = list
    }

    fun getMapLocalLanguages(): HashMap<String,MutableList<LanguageSelectModel>> {
        return localMapLanguageList
    }

    fun removeMapLocalLanguage(serviceId: String) {
        localMapLanguageList.remove(serviceId)
    }

    fun removeAllMapLocalLanguage() {
        localMapLanguageList.clear()
    }

    companion object {
        private lateinit var instance: NSApplication
        private var geoCodeLocationCount: Int = 0

        // App opening alert shown status
        var isAlertShown: Boolean = false

        /**
         * To get the application instance
         *
         * @return The application instance
         */
        fun getInstance(): NSApplication = instance

        /**
         * To check the internet connection status
         *
         * @return Whether internet connected or not
         */
        fun isNetworkConnected(): Boolean = isOnline()

        private fun isOnline(): Boolean {
            val urlConnection =
                URL("https://clients3.google.com/generate_204").openConnection() as HttpsURLConnection
            return try {
                urlConnection.setRequestProperty("User-Agent", "Android")
                urlConnection.setRequestProperty("Connection", "close")
                urlConnection.connectTimeout = 3000
                urlConnection.connect()
                urlConnection.responseCode == 204
            } catch (e: Exception) {
                false
            }
        }
        /**
         * To start the network connection listener
         */
        private fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                } else {
                    @Suppress("DEPRECATION") val networkInfo =
                        connectivityManager.activeNetworkInfo ?: return false
                    @Suppress("DEPRECATION")
                    return networkInfo.isConnected
                }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
            return false
        }
    }
}
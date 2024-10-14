package com.nyotek.dot.admin.common

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.nyotek.dot.admin.common.event.EventHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NSApplication: Application() {

    @Inject
    lateinit var nsLanguageConfig: NSLanguageConfig

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initInstance()
        EventHelper.init()
        nsLanguageConfig.init(applicationContext)
       // NSLanguageConfig().init(applicationContext)
        DeviceDetailUtils.generateUUIDDeviceId()
    }

    private fun initInstance() {
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private lateinit var instance: NSApplication

        fun getInstance(): NSApplication = instance
    }
}
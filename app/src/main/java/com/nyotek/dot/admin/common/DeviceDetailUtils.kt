package com.nyotek.dot.admin.common

import android.content.Context
import android.content.SharedPreferences
import com.nyotek.dot.admin.BuildConfig
import java.util.UUID

object DeviceDetailUtils {

    fun getDeviceId(): String {
        return getBundleId() + "_"+ getUUIDDeviceId()
    }

    private fun getBundleId(): String {
        return BuildConfig.APPLICATION_ID
    }

    fun generateUUIDDeviceId() {
        val sharedPreferences: SharedPreferences = NSApplication.getInstance().applicationContext.getSharedPreferences(NSConstants.DEVICE_ID_STORE,
            Context.MODE_PRIVATE
        )
        val uniId = UUID.randomUUID().toString()
        if (sharedPreferences.getString(NSConstants.DEVICE_ID_STORE_VALUE, "").isNullOrEmpty()) {
            val myEdit = sharedPreferences.edit()
            myEdit.putString(NSConstants.DEVICE_ID_STORE_VALUE, uniId)
            myEdit.apply()
        }
    }

    private fun getUUIDDeviceId(): String {
        val sharedPreferences: SharedPreferences = NSApplication.getInstance().applicationContext.getSharedPreferences(
            NSConstants.DEVICE_ID_STORE,
            Context.MODE_PRIVATE
        )
        val uniId = UUID.randomUUID().toString()
        return if (sharedPreferences.getString(NSConstants.DEVICE_ID_STORE_VALUE, "").isNullOrEmpty()) {
            val myEdit = sharedPreferences.edit()
            myEdit.putString(NSConstants.DEVICE_ID_STORE_VALUE, UUID.randomUUID().toString())
            myEdit.apply()
            sharedPreferences.getString(NSConstants.DEVICE_ID_STORE_VALUE, uniId)?:uniId
        } else {
            sharedPreferences.getString(NSConstants.DEVICE_ID_STORE_VALUE, uniId)?:uniId
        }
    }
}
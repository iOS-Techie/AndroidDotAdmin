package com.nyotek.dot.admin.common

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSRequestCodes.REQUEST_LOCATION_CODE


/**
 * Created by Admin on 25-01-2022.
 */
class NSPermissionHelper(context: Context) {
    private val logTag: String = NSPermissionHelper::class.java.simpleName
    private val nsContext = context
    private var gpsEnabled = false
    private var networkEnabled = false

    /**
     * Is location permission enable
     *
     * @param activity The activity's context
     * @param locationCode location request code
     * @return location permission check
     */
    fun isLocationPermissionEnable(activity: Activity, locationCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                checkLocationPermission(activity, locationCode)
                false
            }
        } else {
            true
        }
    }

    @Suppress("unused")
    fun isLocationBackgroundPermissionEnable(activity: Activity, locationCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    locationCode
                )
                false
            }
        } else {
            true
        }
    }

    /**
     * Is location permission enable
     *
     * @param activity The activity's context
     * @param locationCode location request code
     * @return location permission check
     */
    @Suppress("unused")
    fun isReadPhonePermissionEnable(activity: Activity, locationCode: Int): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                checkPhonePermission(activity, locationCode)
                false
            }
        } else {
            true
        }
    }

    /**
     * Check location permission
     *
     * @param activity The activity's context
     * @param locationCode location request code
     */
    private fun checkPhonePermission(activity: Activity, locationCode: Int) {
        val stringResource = NSApplication.getInstance().getStringModel()
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.location_permission)
                    .setMessage(R.string.location_permission_description)
                    .setPositiveButton(stringResource.ok) { dialog, which ->
                        NSLog.d(logTag, "checkPhonePermission: $dialog $which")
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_CODE
                        )
                    }
                    .create()
                    .show()

            } else ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                locationCode
            )
        }
    }

    /**
     * Check location permission
     *
     * @param activity The activity's context
     * @param locationCode location request code
     */
    private fun checkLocationPermission(activity: Activity, locationCode: Int) {
        val stringResource = NSApplication.getInstance().getStringModel()
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.location_permission)
                    .setMessage(R.string.location_permission_description)
                    .setPositiveButton(stringResource.ok) { dialog, which ->
                        NSLog.d(logTag, "checkLocationPermission: $dialog $which")
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_CODE
                        )
                    }
                    .create()
                    .show()

            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    locationCode
                )
            }
        }
    }

    @Suppress("unused")
    fun isGpsEnable(activity: Activity) {
        val stringResource = NSApplication.getInstance().getStringModel()
        val lm = nsContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gpsEnabled && !networkEnabled) {
            // notify user
            AlertDialog.Builder(activity)
                .setTitle(R.string.gps_network_not_enabled)
                .setMessage(R.string.open_location_settings)
                .setPositiveButton(stringResource.ok) { dialog, which ->
                    NSLog.d(logTag, "checkGpsEnable: $dialog $which")
                    nsContext.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }
                .setNegativeButton(stringResource.cancel, null)
                .create()
                .show()
        }
    }

    private fun isLocationEnabledOrNot(context: Context): Boolean {
        val locationManager: LocationManager? = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showAlertLocation(context: Context, title: String, message: String) {
        val stringResource = NSApplication.getInstance().getStringModel()
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(stringResource.ok) { dialog, which ->
                NSLog.d(logTag, "showAlertLocation: $dialog $which")
                context.startActivity(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                )
            }
            .setNegativeButton(stringResource.cancel, null)
            .create()
            .show()
    }

    @Suppress("unused")
    fun isMyServiceRunning(serviceClass: Class<*>, mActivity: Activity): Boolean {
        val manager: ActivityManager =
            mActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    @Suppress("unused")
    fun checkLocation(activity: Activity) {
        if (!isLocationEnabledOrNot(activity)) {
            showAlertLocation(
                activity,
                activity.resources.getString(R.string.gps_enable),
                activity.resources.getString(R.string.please_turn_on_gps)
            )
        }
    }
}
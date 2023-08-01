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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.NSRequestCodes.REQUEST_LOCATION_CODE
import com.nyotek.dot.admin.common.utils.buildAlertDialog
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutCustomAlertDialogBinding


/**
 * Created by Admin on 25-01-2022.
 */
class NSPermissionHelper(context: Context) {
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
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
                openLocationPermission(activity)
            } else ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                locationCode
            )
        }
    }

    private fun openLocationPermission(activity: Activity) {
        val stringResource = NSApplication.getInstance().getStringModel()
        buildAlertDialog(
            activity,
            LayoutCustomAlertDialogBinding::inflate
        ) { dialog, binding ->
            binding.apply {
                tvOk.text = stringResource.ok
                tvTitle.text = activity.resources.getString(R.string.location_permission)
                tvSubTitle.text = activity.resources.getString(R.string.location_permission_description)
                tvOk.setSafeOnClickListener {
                    dialog.dismiss()
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_CODE
                    )
                }
            }
        }
    }

    /**
     * Check location permission
     *
     * @param activity The activity's context
     * @param locationCode location request code
     */
    private fun checkLocationPermission(activity: Activity, locationCode: Int) {
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
                openLocationPermission(activity)
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
        } catch (_: Exception) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
        }

        if (!gpsEnabled && !networkEnabled) {
            // notify user
            buildAlertDialog(
                activity,
                LayoutCustomAlertDialogBinding::inflate
            ) { dialog, binding ->
                binding.apply {
                    tvOk.text = stringResource.ok
                    tvCancel.text = stringResource.cancel
                    tvCancel.visible()
                    viewLine2.visible()

                    tvTitle.text = activity.resources.getString(R.string.gps_network_not_enabled)
                    tvSubTitle.text = activity.resources.getString(R.string.open_location_settings)
                    tvOk.setSafeOnClickListener {
                        dialog.dismiss()
                        nsContext.startActivity(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        )
                    }

                    tvCancel.setSafeOnClickListener {
                        dialog.dismiss()
                    }
                }
            }
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
        buildAlertDialog(
            context,
            LayoutCustomAlertDialogBinding::inflate
        ) { dialog, binding ->
            binding.apply {
                tvOk.text = stringResource.ok
                tvCancel.text = stringResource.cancel
                tvCancel.visible()
                viewLine2.visible()

                tvTitle.text = title
                tvSubTitle.text = message
                tvOk.setSafeOnClickListener {
                    dialog.dismiss()
                    context.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }

                tvCancel.setSafeOnClickListener {
                    dialog.dismiss()
                }
            }
        }
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
package com.nyotek.dot.admin.common

import android.location.Address
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationResult

/**
 * Class that contains events that used across modules
 */

/**
 * Event that triggered when logout needs to do
 */
class NSLogoutEvent

/**
 * The event that is triggered when a button is clicked in the alert dialog
 */
class NSAlertButtonClickEvent(val buttonType: String, val alertKey: String)

/**
 * Event that triggered when the permission check
 */
class NSPermissionEvent(
    val requestCode: Int,
    val permissions: Array<out String>,
    val grantResults: IntArray
)

class NSOnBackPressEvent

class NSOnBackPressReceiveEvent

class NSOutSideTouchEvent

/**
 * Event that triggered when click on Location data available
 */
class NSAddress(var addresses: List<Address>, var locationResult: LocationResult)

/**
 * The event triggered when the changes occurred in location settings
 *
 * @property isLocationSettingsAvailable status of location settings availability
 */
class NSLocationSettingsEvent(private val isLocationSettingsAvailable: Boolean)

/**
 * Event that triggered when the location changes
 */
class NSLocationChangedEvent(val location: Location?, @Suppress("unused") val errorType: Int)

/**
 * Event that triggered when logout needs to do
 */
class NSRefreshEvent
class NSVendorCall(val fragment: Fragment, val bundle: Bundle)

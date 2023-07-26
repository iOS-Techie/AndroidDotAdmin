package com.nyotek.dot.admin.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.google.android.gms.location.*
import com.nyotek.dot.admin.common.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * The class to handle the location details
 */
class NSLocationManager(context: Context) {
    private val nsContext = context
    private var isAllTimeCall: Boolean = false
    private var isAddressSingleCall: Boolean = false
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val locationRequest: LocationRequest by lazy {
        getLocationRequestObject()
    }
    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                locationAvailability.let {
                    EventBus.getDefault().post(NSLocationSettingsEvent(it.isLocationAvailable))
                }
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation.let { location ->
                     processLocation(location)
                    location?.longitude?.let {
                        getGeoCodeLocation(
                            location.latitude,
                            it, locationResult)
                    }
                }
            }
        }
    }
    private var needToRequestLocation = true  //Need to request the location updates when user becomes to foreground from background
    private var handler: Handler? = Looper.getMainLooper()?.let { Handler(it) }
    private var currentLocation: Location? = null

    companion object {
        private val UPDATE_INTERVAL_IN_MILLISECONDS = TimeUnit.SECONDS.toMillis(5)
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = TimeUnit.SECONDS.toMillis(5)
        private val THIRTY_SECONDS_IN_NANO = TimeUnit.SECONDS.toNanos(5)
        private const val THRESHOLD_ACCURACY = 100.0f
    }

    /**
     * To get the location request object which contains the time to update when in foreground
     *
     * @return The location request object containing the required time
     */
    private fun getLocationRequestObject(): LocationRequest {
        return LocationRequest.create().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            @Suppress("DEPRECATION")
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }
    }

    /**
     * To request the location updates
     *
     * @param looper The message loop for thread
     */
    @SuppressLint("MissingPermission")
    fun requestLocation(looper: Looper?, isSingle: Boolean) {
        isAddressSingleCall = isSingle
        isAllTimeCall = !isSingle
        removeLocation()
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            looper!!
        )
    }

    /**
     * To remove the location callbacks
     */
    fun removeLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * To process the last location received
     *
     * @param location The last location
     */
    private fun processLocation(location: Location?) {
        handler?.removeCallbacksAndMessages(null)
        if (currentLocation == null || needToRequestLocation) {
            postLocation(location, NSLocationType.TYPE_ACCURATE_LOCATION.value)
            needToRequestLocation = false
        }
        if (location?.let { isBetterLocation(it, currentLocation) } == true) {
            currentLocation = location
            postLocation(location, NSLocationType.TYPE_ACCURATE_LOCATION.value)
        } else {
            currentLocation = location
        }
    }

    /**
     * To check the newly received location is better location or not
     *
     * @param location            The newly received location
     * @param currentBestLocation The existing / finally received best location
     * @return Status of the newly received location
     */
    private fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            return true
        }
        // Check whether the new location fix is newer or older
        val timeDelta = location.elapsedRealtimeNanos - currentBestLocation.elapsedRealtimeNanos
        val isSignificantlyNewer: Boolean = timeDelta > THIRTY_SECONDS_IN_NANO
        val isNewer = timeDelta > 0
        val isFutureEntry = location.elapsedRealtimeNanos > SystemClock.elapsedRealtimeNanos()
        if (isFutureEntry) {
            return false
        } else if (isSignificantlyNewer) {
            return true
        } else if (!isNewer) {
            return false
        }
        return location.accuracy <= THRESHOLD_ACCURACY
    }

    /**
     * To post the newly received location
     *
     * @param location The new location
     * @param type     The location type
     */
    private fun postLocation(location: Location?, type: Int) {
        EventBus.getDefault().post(NSLocationChangedEvent(location, type))
    }

    private fun getGeoCodeLocation(latitude: Double, longitude: Double, locationResult: LocationResult) {
        val gcd = Geocoder(
            nsContext,
            Locale.getDefault()
        )
        val addresses: List<Address>
        try {
            @Suppress("DEPRECATION")
            addresses = gcd.getFromLocation(
                latitude,
                longitude, 1
            )?: arrayListOf()
            if (addresses.isNotEmpty()) {
                NSApplication.getInstance().increaseGeoCodeLocationCount()
                if (isAddressSingleCall) {
                    isAddressSingleCall = false
                    isAllTimeCall = false
                    removeLocation()
                    EventBus.getDefault().post(NSAddress(addresses, locationResult))
                } else if (isAllTimeCall){
                    EventBus.getDefault().post(NSAddress(addresses, locationResult))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
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
import com.nyotek.dot.admin.common.utils.NSUtilities
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

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
    @Suppress("DEPRECATION")
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

    fun calculateDurationDistance(lat1: Double?, lon1: Double?, lat2: Double?, lon2: Double?, callback: (String, Double) -> Unit) {
        val distance =  meterDistanceBetweenPoints(lat1?:0.0, lon1?:0.0, lat2?:0.0, lon2?:0.0)
        callback.invoke(calculateDuration(distance), distance)
    }

    private fun meterDistanceBetweenPoints(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {

        val earthRadiusKm: Double = 6371.0

        val dLat = degreesToRadians(lat2 - lat1)
        val dLon = degreesToRadians(lon2 - lon1)

        val latA = degreesToRadians(lat1)
        val latB = degreesToRadians(lat2)

        val a = sin(dLat/2) * sin(dLat/2) +
                sin(dLon/2) * sin(dLon/2) * cos(latA) * cos(latB)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return NSUtilities.convertToArabic((earthRadiusKm * c).toString()).toDouble()
    }

    private fun degreesToRadians(degrees: Double):Double {
        return degrees * Math.PI / 180
    }
    private fun calculateDuration(distance: Double): String {
        val hours = (distance/50)
        val remainder = hours.rem(1) * 60
        val hrs = hours.roundToInt()
        val minutes = remainder.roundToInt()

        if (hrs > 0 && minutes > 0) {
            return "$hrs h $minutes Min"
        } else if (hrs > 0) {
            return "$hrs h"
        } else if (minutes > 0) {
            return "$minutes Min"
        }

        return "1 Min"
    }
}
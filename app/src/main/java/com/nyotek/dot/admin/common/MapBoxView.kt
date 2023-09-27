package com.nyotek.dot.admin.common

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import android.view.View
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.mapbox.common.TileStore
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.GeoJSONSourceData
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.Style
import com.mapbox.maps.TileStoreUsageMode
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.plugin.animation.CameraAnimationsLifecycleListener
import com.mapbox.maps.plugin.animation.CameraAnimatorType
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.callbacks.NSMapDriverClickCallback
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.InfoWindowMultilineBinding
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import java.util.logging.Handler
import kotlin.math.abs


class MapBoxView(private val context: Context) {

    private var fleetDataItem: FleetDataItem? = null
    private var viewAnnotationManager: ViewAnnotationManager? = null
    private var viewAnnotation: View? = null
    private var map: MapboxMap? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var styleMap: Style? = null
    private var mapMarker: Bitmap? = null
    private var mapView: MapView? = null
    private var callback: NSMapDriverClickCallback? = null
    private var isDialogDisplay: Boolean = false
    private val tileStore by lazy { TileStore.create() }

    init {
        ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_ACCESS_TOKEN).update {
            tileStoreUsageMode(TileStoreUsageMode.READ_ONLY)
        }
//        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
//        MapboxOptions.mapsOptions.tileStore = tileStore
//        MapboxOptions.mapsOptions.tileStoreUsageMode = TileStoreUsageMode.READ_ONLY

    }

    fun initMapView(context: Context, view: MapView, fleetData: FleetDataItem?, mapStyle: String = Style.MAPBOX_STREETS, mapCallback: NSMapDriverClickCallback? = null) {
        fleetDataItem = fleetData
        callback = mapCallback
        mapView = view
        map = view.getMapboxMap()
        viewAnnotationManager = view.viewAnnotationManager
        initAddMarker(context, mapStyle)
        initStyleMap()
    }

    private fun initStyleMap() {
        fleetDataItem?.apply {
            if (styleMap == null) {
                if (features.isEmpty()) {
                    moveCamera(Point.fromLngLat(longitude, latitude), 0.0, 0.0)
                } else {
                    val model = features[0]
                    goToMapPosition(model.properties?.fleetId ?: "", 0.0, 0.0)
                }
            }
        }
    }

    fun setCurrentLatLong(lat: Double, lon: Double) {
        latitude = lat
        longitude = lon
    }

    fun getCurrentPosition(): LatLng {
        val cameraPosition = map?.getFreeCameraOptions()
        val latitude = cameraPosition?.position?.x
        val longitude = cameraPosition?.position?.y
        return LatLng(latitude?:0.0, longitude?:0.0)
    }

    fun goToMapPosition(fleetId: String, zoom: Double = 13.0, pitch: Double = 10.0) {
        for (data in fleetDataItem?.features ?: arrayListOf()) {
            if (data.properties?.fleetId.equals(fleetId)) {
                moveCamera(
                    Point.fromLngLat(
                        data.properties?.longitude ?: 0.0, data.properties?.latitude ?: 0.0
                    ), zoom, pitch
                )
            }
        }
    }

    public fun moveCamera(point: Point, zoom: Double = 13.0, pitch: Double = 10.0) {
        val cameraPosition = CameraOptions.Builder()
            .center(point)
            .zoom(zoom)
            .pitch(pitch)
            .build()
        // set camera position
        map?.setCamera(cameraPosition)
    }

    private fun initAddMarker(context: Context, mapStyle: String) {
        if (styleMap == null) {
            map?.loadStyleUri(mapStyle) { style ->
                styleMap = style
                mapMarker = BitmapUtils.bitmapFromDrawableRes(context, R.drawable.ic_map_marker)!!
                styleMap?.addImage(
                    NSConstants.ICON_ID, mapMarker!!
                )
                // Create a SymbolLayer to render the symbols
                val stretchLayer = symbolLayer(NSConstants.LAYER_ID, NSConstants.SOURCE_ID) {
                    iconImage(NSConstants.ICON_ID)
                    iconAllowOverlap(true)
                    iconSize(1.0)
                }
                styleMap?.addLayer(stretchLayer)

                map?.addOnMapClickListener { point ->
//                    android.os.Handler(Looper.getMainLooper()).postDelayed( {
//                        moveCamera(point, 14.0, 10.0)
//                    }, 3000)
                    //moveCamera(point, 14.0, 10.0)
                    map?.queryRenderedFeatures(
                        RenderedQueryGeometry(map!!.pixelForCoordinate(point)),
                        RenderedQueryOptions(listOf(NSConstants.LAYER_ID), null)
                    ) {
                        if (it.value.isValidList()) {
                            removeAnnotation()
                            //_addViewAnnotation(point)
                            prepareViewAnnotation(
                                it.value?.get(0)?.feature
                            )
                        } else {
                            removeAnnotation()
                        }
                    }
                    true
                }

                loadMap(false)
            }
        } else {
            loadMap(styleMap?.getSource(NSConstants.SOURCE_ID) != null)
        }
    }

    private fun loadMap(isUpdate: Boolean) {
        if (fleetDataItem?.features.isValidList()) {
            if (isUpdate) {
                val featureCollection = FeatureCollection.fromJson(Gson().toJson(fleetDataItem ?: FleetDataItem()))
                if (featureCollection.features() != null) {
                    styleMap?.setStyleGeoJSONSourceData(
                        NSConstants.SOURCE_ID, "",
                        GeoJSONSourceData.valueOf(featureCollection.features()!!)
                    )
                }
            } else {
                val featureCollection =
                    FeatureCollection.fromJson(Gson().toJson(fleetDataItem ?: FleetDataItem()))
                val builder = GeoJsonSource.Builder(NSConstants.SOURCE_ID)
                    .featureCollection(featureCollection)
                styleMap?.addSource(builder.build())
            }
        }
    }

    private fun removeAnnotation() {
        viewAnnotation?.let { viewAnnotationManager?.removeViewAnnotation(it) }
        if (isDialogDisplay) {
            isDialogDisplay = false
            callback?.onDriverMap("", true)
        }
    }

    private fun prepareViewAnnotation(feature: Feature?) {
        val difference = ((map?.cameraState?.zoom ?: 0.0) - 17.0f)
        var zoom = 17.0
        var duration = 2.0
        if (difference >= 0) {
            zoom = map?.cameraState?.zoom ?: 17.0
            duration = 0.0
        } else {
            duration = abs(difference / 5)
        }
        val finalDuration = duration * 1000

        val point: Point = Point.fromLngLat((feature?.getProperty("longitude")?:"0.0").toString().toDouble(), (feature?.getProperty("latitude")?:"0.0").toString().toDouble())

        val camera = CameraOptions.Builder()
            .center(point)
            .zoom(zoom)
            .pitch(0.0)
            .anchor(ScreenCoordinate(point.latitude(), point.longitude()))
            .build()

        val cancellable = mapView?.camera?.flyTo(
            camera,
            mapAnimationOptions {
                duration(finalDuration.toLong())
            })

        var isAdd: Boolean = true

        fun callDialogMap() {
            viewAnnotation = viewAnnotationManager?.addViewAnnotation(
                resId = R.layout.info_window_multiline,
                options = viewAnnotationOptions {
                    geometry(point)
                    anchor(ViewAnnotationAnchor.BOTTOM)
                    allowOverlap(false)
                }
            )

            isDialogDisplay = true
            InfoWindowMultilineBinding.bind(viewAnnotation!!).apply {
                val stringResource = StringResourceResponse()

                val driverIdValue = feature?.getStringProperty("driver_id") ?: ""
                val driverTitle = stringResource.driver + ":"
                layoutDriverId.tvTitleMap.text = driverTitle
                layoutDriverId.tvValueMap.text = driverIdValue

                val locationTitle = stringResource.location + ":"
                layoutLocation.tvTitleMap.text = locationTitle
                val latLong = "%.6f, %.6f".format(point.latitude(), point.longitude())
                layoutLocation.tvValueMap.text = latLong

                val vehicleIdValue = feature?.getStringProperty("vehicle_id") ?: ""
                val vehicleTitle = stringResource.vehicle + ":"
                layoutVehicle.tvTitleMap.text = vehicleTitle
                layoutVehicle.tvValueMap.text = vehicleIdValue

                val fleetValue = feature?.getStringProperty("fleet_id") ?: ""
                val fleetTitle = stringResource.fleet + ":"
                layoutFleet.tvTitleMap.text = fleetTitle
                layoutFleet.tvValueMap.text = fleetValue

                val driverStatusValue = feature?.getStringProperty("driver_status") ?: ""
                val driverStatusTitle = stringResource.driverStatus + ":"
                layoutDriverStatus.tvTitleMap.text = driverStatusTitle
                layoutDriverStatus.tvValueMap.text = driverStatusValue

                val dispatchIdValue = feature?.getStringProperty("ref_id") ?: ""
                val dispatchIdTitle = stringResource.dispatchId + ":"
                layoutDispatchId.tvTitleMap.text = dispatchIdTitle
                layoutDispatchId.tvValueMap.text = dispatchIdValue

                val activeDispatchesValue =
                    feature?.getStringProperty("dispatch_count")?.toDouble()?.toInt() ?: 0
                val activeDispatchTitle = stringResource.activeDispatches + ":"
                layoutActiveDispatches.tvTitleMap.text = activeDispatchTitle
                layoutActiveDispatches.tvValueMap.text = activeDispatchesValue.toString()

                if (activeDispatchesValue > 0) {
                    layoutActiveDispatches.clMapDriverView.visible()
                    layoutActiveDispatches.tvSeeAll.text = stringResource.seeAll
                    layoutActiveDispatches.tvSeeAll.visible()

                    layoutActiveDispatches.tvSeeAll.setSafeOnClickListener {
                        callback?.onDriverMap(driverIdValue, false)
                    }
                } else {
                    layoutActiveDispatches.clMapDriverView.gone()
                }

                val timeValue = feature?.getStringProperty("created_at") ?: ""
                val timeTitle = stringResource.time + ":"
                layoutTime.tvTitleMap.text = timeTitle
                layoutTime.tvValueMap.text =
                    NSDateTimeHelper.formatDateToNowOrDateTime(timeValue)
            }
        }

        callDialogMap()

            /*ivClose.setOnClickListener {
                viewAnnotationManager?.removeViewAnnotation(viewAnnotation!!)
            }*/
    }

    private fun _addViewAnnotation(coordinate: Point) {
        val difference = ((map?.cameraState?.zoom ?: 0.0) - 17.0f)
        var zoom = 17.0
        var duration = 2.0
        if (difference >= 0) {
            zoom = map?.cameraState?.zoom ?: 17.0
            duration = 0.0
        } else {
            duration = Math.abs(difference / 5)
        }

        val camera = CameraOptions.Builder()
            .center(coordinate)
            .zoom(zoom)
            .build()


        map?.flyTo(
            camera,
            mapAnimationOptions {
                duration(duration.toLong())
            }
        )

//        val options = MapAnimationOptions.Builder()
//        options.duration(5)
//        options.startDelay(0)
//      //  options.interpolator(AccelerateDecelerateInterpolator())
//        //(markerId, duration.toLong(), 0, AccelerateDecelerateInterpolator())
//
//        mapView?.camera?.easeTo(camera, options.build(), object : Animator.AnimatorListener {
//            override fun onAnimationStart(p0: Animator) {
//
//            }
//
//            override fun onAnimationEnd(p0: Animator) {
//                val options = ViewAnnotationOptions.Builder()
//                    .geometry(coordinate)
//                    .width(391)
//                    .height(270)
//                    //.associatedFeatureId(markerId)
//                    .allowOverlap(false)
//                    .anchor(ViewAnnotationAnchor.BOTTOM)
//                    .build()
//
//            }
//
//            override fun onAnimationCancel(p0: Animator) {
//
//            }
//
//            override fun onAnimationRepeat(p0: Animator) {
//
//            }
//
//
//        })
//        mapView?.camera?.easeTo(camera, duration, object : CancelableCallback {
//            override fun onCancel() {
//                // Handle cancellation if needed
//            }
//
//            override fun onFinish() {
//                val options = ViewAnnotationOptions.Builder()
//                    .geometry(Point.fromLngLat(coordinate.longitude, coordinate.latitude))
//                    .width(391.0)
//                    .height(270.0)
//                    .associatedFeatureId(markerId)
//                    .allowOverlap(false)
//                    .anchor(Anchor.BOTTOM)
//                    .build()
//
//                val annotationView = AnnotationView.instanceFromNib()
//                annotationView.title = markerId
//                annotationView.location = String.format("%.6f, %.6f", coordinate.latitude, coordinate.longitude)
//
//                val feature = _featureCollection?.features?.firstOrNull { it.properties?.rawValue?.get("driver_id") as? String == markerId }
//                val property = feature?.properties
//
//                if (property != null) {
//                    val vehicleId = property.rawValue["vehicle_id"] as? String
//                    if (!Utils.stringIsNullOrEmpty(vehicleId)) {
//                        annotationView.vehicle = vehicleId
//                    }
//
//                    val fleet = property.rawValue["fleet_id"] as? String
//                    if (fleet != null) {
//                        val name = NYOFLEET.getFleetNameById(fleetId = fleet)
//                        annotationView.fleet = name ?: fleet
//                    }
//
//                    val driverStatus = property.rawValue["driver_status"] as? String
//                    if (!Utils.stringIsNullOrEmpty(driverStatus)) {
//                        annotationView.driverStatus = driverStatus?.capitalize()
//                    }
//
//                    val refId = property.rawValue["ref_id"] as? String
//                    if (!Utils.stringIsNullOrEmpty(refId)) {
//                        annotationView.refId = refId
//                    }
//
//                    val dispatchCount = property.rawValue["dispatch_count"] as? Double
//                    if (dispatchCount != null && dispatchCount > 0) {
//                        println("dispatch_count Inside : $dispatchCount")
//                        annotationView.dispatchCount = dispatchCount.toInt().toString()
//                    }
//
//                    val ts = property.rawValue["ts"] as? String
//                    if (ts != null) {
//                        val date = Utils.stringToDate(ts, format = kFormatDateISO8601UTC2)
//                        annotationView.time = date?.timeAgoSince ?: ts
//                    }
//                }
//
//                annotationView.setUpData {
//                    println("See all called driver id : $markerId")
//                    _getDispatchListByDriver(driverId = markerId ?: "")
//                }
//
//                try {
//                    _mapView?.viewAnnotations?.add(annotationView, options)
//                    _mapView?.viewAnnotations?.update(annotationView, ViewAnnotationOptions.Builder()
//                        .offsetY(markerHeight)
//                        .build()
//                    )
//                } catch (e: Exception) {
//                    // Handle exceptions if necessary
//                }
//            }
//        })
    }
}
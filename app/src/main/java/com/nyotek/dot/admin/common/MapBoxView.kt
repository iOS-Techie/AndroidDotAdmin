package com.nyotek.dot.admin.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.FrameLayout
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
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.callbacks.NSMapDriverClickCallback
import com.nyotek.dot.admin.common.utils.NSLanguageConfig
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.InfoWindowMultilineBinding
import com.nyotek.dot.admin.repository.network.responses.FeaturesItem
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    @SuppressLint("ClickableViewAccessibility")
    fun initMapView(context: Context, frameLayout: FrameLayout, fleetData: FleetDataItem?, mapStyle: String = Style.MAPBOX_STREETS, mapCallback: NSMapDriverClickCallback? = null) {
        fleetDataItem = fleetData
        callback = mapCallback
        mapView?.removeAllViews()
        val view = MapView(context)
        frameLayout.removeAllViews()
        frameLayout.addView(view)
        mapView = view
        view.layoutDirection = View.LAYOUT_DIRECTION_LTR
        mapView?.setOnTouchListener { view, _ ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        map = view.getMapboxMap()
        viewAnnotationManager = view.viewAnnotationManager
        initAddMarker(context, mapStyle)
        initStyleMap()
    }

    fun initMapView(context: Context, view: MapView, fleetData: FleetDataItem?, mapStyle: String = Style.MAPBOX_STREETS, mapCallback: NSMapDriverClickCallback? = null) {
        fleetDataItem = fleetData
        callback = mapCallback
        view.layoutDirection = View.LAYOUT_DIRECTION_LTR
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
                        moveCamera(Point.fromLngLat(longitude, latitude), 0.5, 0.0)
                } else {
                    val model = features[0]
                    goToMapPosition(model.properties?.fleetId ?: "", 0.5, 0.0)
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

    fun goToDispatchMapPosition(list: List<FeaturesItem>?, zoom: Double = 17.0, pitch: Double = 10.0) {
        if (list.isValidList()) {
            val featureData = list?.first()
            val property = featureData?.properties
            val latitude = property?.latitude
            val longitude = property?.longitude
            moveCamera(
                Point.fromLngLat(
                    longitude ?: 0.0, latitude ?: 0.0
                ), zoom, pitch
            )
        }
    }

    fun moveCamera(point: Point, zoom: Double = 13.0, pitch: Double = 10.0) {
        val cameraPosition = CameraOptions.Builder()
            .center(point)
            .zoom(zoom)
            .pitch(pitch)
            .build()
        // set camera position
        map?.setCamera(cameraPosition)
    }

    fun getMapView(): MapView? {
        return mapView
    }

    fun clearMap() {
        mapView?.removeAllViews()
        map = null
        mapMarker = null
        styleMap = null
        fleetDataItem = null
        viewAnnotationManager = null
        viewAnnotation = null
        map = null
        latitude = 0.0
        longitude = 0.0
        styleMap = null
        mapMarker = null
        mapView = null
        callback = null
        isDialogDisplay = false
    }

    fun clearMapView() {
        map = null
        mapMarker = null
        styleMap = null
        fleetDataItem = null
        viewAnnotationManager = null
        viewAnnotation = null
        map = null
        latitude = 0.0
        longitude = 0.0
        styleMap = null
        mapMarker = null
        mapView = null
        callback = null
        isDialogDisplay = false
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadMap(isUpdate: Boolean) {
        if (fleetDataItem?.features.isValidList()) {
            GlobalScope.launch(Dispatchers.Main) {
                if (isUpdate) {
                    updateGeoJsonSourceAsync()
                } else {
                    addGeoJsonSourceAsync()
                }
            }
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                setEmptyGeoJsonSourceAsync()
            }
        }
    }

    private suspend fun updateGeoJsonSourceAsync() {
        val featureCollection = withContext(Dispatchers.IO) {
            FeatureCollection.fromJson(Gson().toJson(fleetDataItem ?: FleetDataItem()))
        }

        if (featureCollection.features() != null) {
            withContext(Dispatchers.Main) {
                styleMap?.setStyleGeoJSONSourceData(
                    NSConstants.SOURCE_ID, "",
                    GeoJSONSourceData.valueOf(featureCollection.features()!!)
                )
            }
        }
    }

    private suspend fun addGeoJsonSourceAsync() {
        val featureCollection = withContext(Dispatchers.IO) {
            FeatureCollection.fromJson(Gson().toJson(fleetDataItem ?: FleetDataItem()))
        }

        val builder = GeoJsonSource.Builder(NSConstants.SOURCE_ID)
            .featureCollection(featureCollection)

        withContext(Dispatchers.Main) {
            if (styleMap?.getSource(NSConstants.SOURCE_ID) == null) {
                styleMap?.addSource(builder.build())
            }
        }
    }

    private suspend fun setEmptyGeoJsonSourceAsync() {
        val emptyFeatureCollection = withContext(Dispatchers.IO) {
            FeatureCollection.fromFeatures(ArrayList())
        }

        if (emptyFeatureCollection.features() != null) {
            withContext(Dispatchers.Main) {
                styleMap?.setStyleGeoJSONSourceData(
                    NSConstants.SOURCE_ID, "",
                    GeoJSONSourceData.valueOf(emptyFeatureCollection.features()!!)
                )
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
        val duration: Double
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

        mapView?.camera?.flyTo(
            camera,
            mapAnimationOptions {
                duration(finalDuration.toLong())
            })

        fun callDialogMap() {
            viewAnnotation = viewAnnotationManager?.addViewAnnotation(
                resId = R.layout.info_window_multiline,
                options = viewAnnotationOptions {
                    geometry(point)
                    anchor(ViewAnnotationAnchor.BOTTOM)
                    allowOverlap(false)
                }
            )
            viewAnnotation?.layoutDirection = if(NSLanguageConfig.isLanguageRtl()) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
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
}
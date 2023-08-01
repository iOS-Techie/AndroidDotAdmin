package com.nyotek.dot.admin.common

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.GeoJSONSourceData
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.Style
import com.mapbox.maps.TileStoreUsageMode
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.nyotek.dot.admin.BuildConfig
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.InfoWindowMultilineBinding
import com.nyotek.dot.admin.repository.network.responses.FleetDataItem


class MapBoxView(context: Context) {

    private var fleetDataItem: FleetDataItem? = null
    private var viewAnnotationManager: ViewAnnotationManager? = null
    private var viewAnnotation: View? = null
    private var map: MapboxMap? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var styleMap: Style? = null
    private var mapMarker: Bitmap? = null

    init {
        ResourceOptionsManager.getDefault(context, BuildConfig.MAPBOX_ACCESS_TOKEN).update {
            tileStoreUsageMode(TileStoreUsageMode.READ_ONLY)
        }
    }

    fun initMapView(context: Context, view: MapView, fleetData: FleetDataItem?, mapStyle: String = Style.MAPBOX_STREETS) {
        fleetDataItem = fleetData
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
                    map?.queryRenderedFeatures(
                        RenderedQueryGeometry(map!!.pixelForCoordinate(point)),
                        RenderedQueryOptions(listOf(NSConstants.LAYER_ID), null)
                    ) {
                        if (it.value.isValidList()) {
                            removeAnnotation()
                            prepareViewAnnotation(
                                mapMarker?.height ?: 1,
                                point,
                                it.value?.get(0)?.feature?.getStringProperty("driver_id") ?: ""
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
    }

    private fun prepareViewAnnotation(height: Int, point: Point, driverId: String) {
        viewAnnotation = viewAnnotationManager?.addViewAnnotation(
            resId = R.layout.info_window_multiline,
            options = viewAnnotationOptions {
                geometry(point)
                // associatedFeatureId(markId)
                anchor(ViewAnnotationAnchor.BOTTOM)
                offsetY(height)
            }
        )
        InfoWindowMultilineBinding.bind(viewAnnotation!!).apply {
            val stringResource = NSApplication.getInstance().getStringModel()
            val driverIdValue =stringResource.driver + " = $driverId"
            tvTitleMap.text = driverIdValue
            tvSnippet.visible()
            val latLong = stringResource.latShort + " = %.2f\n${stringResource.longShort} = %.2f".format(point.latitude(), point.longitude())
            tvSnippet.text = latLong
            ivClose.setOnClickListener {
                viewAnnotationManager?.removeViewAnnotation(viewAnnotation!!)
            }
        }
    }
}
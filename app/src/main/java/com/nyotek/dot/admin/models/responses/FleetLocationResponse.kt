package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class FleetLocationResponse(

	@field:SerializedName("data")
	val fleetDataItem: FleetDataItem? = null
)

data class FeaturesItem(

	@field:SerializedName("geometry")
	val geometry: Geometry? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("is_employee_selected")
	var isEmployeeSelected: Boolean = false
)

data class Geometry(

	@field:SerializedName("coordinates")
	val coordinates: MutableList<Double> = arrayListOf(),

	@field:SerializedName("type")
	val type: String? = null
)

data class DriverListModel(

	@field:SerializedName("data")
	val driverList: MutableList<Properties>? = arrayListOf()
)

data class FleetDataItem(

	@field:SerializedName("features")
    var features: List<FeaturesItem> = arrayListOf(),

	@field:SerializedName("type")
	val type: String? = null
)

data class Properties(

	@field:SerializedName("ref_id")
	val refId: String? = null,

	@field:SerializedName("altitude")
	val altitude: Float = 0f,

	@field:SerializedName("driver_id")
	val driverId: String? = null,

	@field:SerializedName("capabilities")
	val capabilities: MutableList<String> = arrayListOf(),

	@field:SerializedName("is_active")
	val isActive: Boolean = false,

	@field:SerializedName("bearing_accuracy")
	val bearingAccuracy: Float = 0f,

	@field:SerializedName("horizontal_accuracy")
	val horizontalAccuracy: Float = 0f,

	@field:SerializedName("bearing")
	val bearing: Float = 0f,

	@field:SerializedName("latitude")
	val latitude: Double = 0.0,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("speed")
	val speed: Double = 0.0,

	@field:SerializedName("vertical_accuracy")
	val verticalAccuracy: Float = 0f,

	@field:SerializedName("speed_accuracy")
	val speedAccuracy: Float = 0f,

	@field:SerializedName("fleet_id")
	val fleetId: String? = null,

	@field:SerializedName("driver_status")
	val driverStatus: String? = null,

	@field:SerializedName("ref_type")
	val refType: String? = null,

	@field:SerializedName("ref_status")
	val refStatus: String? = null,

	@field:SerializedName("longitude")
	val longitude: Double = 0.0,

	@field:SerializedName("dest_lng")
	val destLongitude: Double = 0.0,

	@field:SerializedName("dest_lat")
	val destLatitude: Double = 0.0,

	@field:SerializedName("ts")
	val ts: String? = null,

	@field:SerializedName("dest_geom")
	val destGeom: String? = null,

	@field:SerializedName("dispatch_count")
	val dispatchCount: Int = 0,

	@field:SerializedName("vehicle_id")
	val vehicleId: String? = ""
)

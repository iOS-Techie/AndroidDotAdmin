package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.SerializedName

data class NSVehicleRequest(

	@field:SerializedName("ref_id")
	var refId: String? = null,

	@field:SerializedName("capabilities")
	var capabilities: MutableList<String> = arrayListOf(),

	@field:SerializedName("is_active")
	val isActive: Boolean? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("vehicle_img")
	var vehicleImg: String? = null,

	@field:SerializedName("load_capacity")
	var loadCapacity: String? = null,

	@field:SerializedName("manufacturing_year")
	var manufacturingYear: String? = null,

	@field:SerializedName("registration_no")
	var registrationNo: String? = null,

	@field:SerializedName("manufacturer")
	var manufacturer: String? = null,

	@field:SerializedName("is_deleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("model")
	var model: String? = null,

	@field:SerializedName("ref_type")
	var refType: String? = null,

	@field:SerializedName("additional_note")
	var additionalNote: String? = null
)

data class NSVehicleEnableDisableRequest(

	@field:SerializedName("vehicle_id")
	var vehicleId: String? = null
)

data class NSVehicleNotesRequest(

	@field:SerializedName("id")
	var id: String? = null,

	@field:SerializedName("additional_note")
	var additionalNote: String? = null
)

data class NSVehicleUpdateImageRequest(

	@field:SerializedName("id")
	var id: String? = null,

	@field:SerializedName("vehicle_img")
	var vehicle_img: String? = null
)

data class NSAssignVehicleRequest(

	@field:SerializedName("driver_id")
	val driver_id: String? = null,

	@field:SerializedName("fleet_id")
	val fleet_id: String? = null,

	@field:SerializedName("vehicle_id")
	val vehicle_id: String? = null,

	@field:SerializedName("capabilities")
	val capabilityId: MutableList<String> = arrayListOf()
)

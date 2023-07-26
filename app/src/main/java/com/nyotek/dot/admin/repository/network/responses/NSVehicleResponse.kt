package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSVehicleResponse(

	@field:SerializedName("data")
	val data: MutableList<VehicleDataItem> = arrayListOf()
)

data class VehicleDataItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("ref_id")
	val refId: String? = null,

	@field:SerializedName("ref_type")
	val refType: String? = null,

	@field:SerializedName("capabilities")
    var capabilities: MutableList<String> = arrayListOf(),

	@field:SerializedName("is_active")
	var isActive: Boolean = false,

	@field:SerializedName("registration_no")
	val registrationNo: String? = null,

	@field:SerializedName("manufacturer")
	val manufacturer: String? = null,

	@field:SerializedName("model")
	val model: String? = null,

	@field:SerializedName("manufacturing_year")
	val manufacturingYear: String? = null,

	@field:SerializedName("vehicle_img")
	val vehicleImg: String? = null,

	@field:SerializedName("load_capacity")
	val loadCapacity: String? = null,

	@field:SerializedName("additional_note")
    var additionalNote: String? = null,

	@field:SerializedName("is_deleted")
	var isDeleted: Boolean = false,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,
)

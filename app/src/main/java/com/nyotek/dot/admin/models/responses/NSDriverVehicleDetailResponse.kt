package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class NSDriverVehicleDetailResponse(

	@field:SerializedName("data")
	val data: VehicleData? = null
)

data class VehicleData(

	@field:SerializedName("ref_id")
	val refId: String? = null,

	@field:SerializedName("capabilities")
	val capabilities: MutableList<String> = arrayListOf(),

	@field:SerializedName("is_active")
	val isActive: Boolean = false,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("vehicle_img")
	val vehicleImg: String? = null,

	@field:SerializedName("load_capacity")
	val loadCapacity: String? = null,

	@field:SerializedName("manufacturing_year")
	val manufacturingYear: String? = null,

	@field:SerializedName("registration_no")
	val registrationNo: String? = null,

	@field:SerializedName("manufacturer")
	val manufacturer: String? = null,

	@field:SerializedName("is_deleted")
	val isDeleted: Boolean = false,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("model")
	val model: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("ref_type")
	val refType: String? = null,

	@field:SerializedName("additional_note")
	val additionalNote: String? = null
)

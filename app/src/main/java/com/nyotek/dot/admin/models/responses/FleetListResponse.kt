package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class FleetListResponse(

	@field:SerializedName("data")
	val data: List<FleetData> = arrayListOf()
)

data class FleetSingleResponse(
	@field:SerializedName("data")
	val data: FleetData? = null
)

data class FleetServiceResponse(
	@field:SerializedName("fleet_data")
	val data: FleetData? = null,

	@field:SerializedName("is_selected")
	val isSelected: Boolean = false
)

data class FleetData(

	@field:SerializedName("logo_scale")
    var logoScale: String? = null,

	@field:SerializedName("is_active")
	var isActive: Boolean = false,

	@field:SerializedName("owner_id")
	val ownerId: String? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("address_id")
    var addressId: String? = null,

	@field:SerializedName("verified")
	val verified: Boolean = false,

	@field:SerializedName("url")
	var url: String? = null,

	@field:SerializedName("tags")
    var tags: MutableList<String>? = arrayListOf(),

	@field:SerializedName("vendor_id")
	var vendorId: String? = null,

	@field:SerializedName("name")
	val name: HashMap<String, String> = hashMapOf(),

	@field:SerializedName("logo")
    var logo: String? = null,

	@field:SerializedName("service_ids")
	val serviceIds: MutableList<String> = arrayListOf(),

	@field:SerializedName("logo_height")
	val logoHeight: Int = 0,

	@field:SerializedName("last_modified")
	val lastModified: String? = null,

	@field:SerializedName("logo_width")
	val logoWidth: Int = 0,

	@field:SerializedName("schedule_id")
	val scheduleId: String? = null,

	@field:SerializedName("slogan")
	val slogan: HashMap<String, String> = hashMapOf(),

	@field:SerializedName("status")
	val status: String? = null,

	//Set Address
	@field:SerializedName("address_model")
	var addressModel: AddressData? = null


)
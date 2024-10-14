package com.nyotek.dot.admin.models.requests

import com.google.gson.annotations.SerializedName

data class NSFleetRequest(

	@field:SerializedName("vendor_id")
	val vendorId: String? = null
)

data class NSFleetLogoUpdateRequest(

	@field:SerializedName("vendor_id")
	var vendorId: String? = null,

	@field:SerializedName("logo")
	var logo: String = "",

	@field:SerializedName("logo_width")
	var logoWidth: Int = 0,

	@field:SerializedName("logo_height")
	var logoHeight: Int = 0
)

data class NSFleetLogoScaleRequest(

	@field:SerializedName("vendor_id")
	var vendorId: String? = null,

	@field:SerializedName("logo_scale")
	var logoScale: String = ""
)

data class NSFleetNameUpdateRequest(

	@field:SerializedName("vendor_id")
	var vendorId: String? = null,

	@field:SerializedName("name")
	var name: HashMap<String, String> = hashMapOf()
)

data class NSFleetServiceIdsUpdateRequest(

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("service_ids")
	val serviceIds: List<String> = arrayListOf()
)

data class NSFleetSloganUpdateRequest(

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("slogan")
	val slogan: HashMap<String, String> = hashMapOf()
)

data class NSFleetUpdateTagsRequest(

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("tags")
	val tags: List<String> = arrayListOf()
)

data class NSFleetAddRemoveTagsRequest(
	
	@field:SerializedName("vendor_id")
	val vendorId: String? = null,
	
	@field:SerializedName("tag")
	val tag: String? = null
)

data class NSFleetUrlUpdateRequest(

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class NSServiceFleetUpdateRequest(

	@field:SerializedName("service_id")
	val serviceId: String? = null,

	@field:SerializedName("fleet_id")
	val fleetId: String? = null
)
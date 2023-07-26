package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSGetServiceListResponse(
	@field:SerializedName("data")
	val data: MutableList<NSGetServiceListData> = arrayListOf()
)

data class NSCreateServiceResponse(
	@field:SerializedName("data")
	val data: NSGetServiceListData? = null
)

data class NSGetServiceListData(

	@field:SerializedName("is_active")
	var isActive: Boolean = false,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("service_id")
	val serviceId: String? = null,

	/*This Add from capability service API*/
	@field:SerializedName("capability_item")
	var capabilityItem: ServiceCapabilitiesDataItem? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("last_modified")
	val lastModified: String? = null,

	@field:SerializedName("get_nearest_url")
	val getNearestUrl: String? = null
)

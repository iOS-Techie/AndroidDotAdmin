package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class NSServiceCapabilityResponse(

	@field:SerializedName("data")
	val data:ServiceCapabilitiesDataItem? = null
)

data class ServiceCapabilitiesDataItem(

	@field:SerializedName("service_id")
    var serviceId: String? = null,

	@field:SerializedName("capability_id")
    var capabilityId: String? = null,

	@field:SerializedName("fleets")
    var fleets: MutableList<String> = arrayListOf(),

	@field:SerializedName("created_at")
	var createdAt: Boolean = false,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null
)
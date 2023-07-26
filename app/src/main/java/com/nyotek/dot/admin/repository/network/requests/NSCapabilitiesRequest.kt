package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NSCapabilitiesRequest(

	@field:SerializedName("capability_id")
	val capabilityId: String? = null
)

open class NSCreateCapabilityRequest(
	@SerializedName("label")
	@Expose
	var label: HashMap<String, String> = hashMapOf()
)

data class NSServiceCapabilitiesRequest(

	@field:SerializedName("service_id")
	val service_id: String? = null,

	@field:SerializedName("capability_id")
	val capabilityId: String? = null
)

data class NSUpdateCapabilitiesRequest(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("capabilities")
	val capabilityId: MutableList<String> = arrayListOf()
)

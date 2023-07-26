package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.SerializedName

data class NSServiceRequest(

	@field:SerializedName("service_id")
	val serviceId: String? = null
)

data class NSCreateServiceRequest(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null
)

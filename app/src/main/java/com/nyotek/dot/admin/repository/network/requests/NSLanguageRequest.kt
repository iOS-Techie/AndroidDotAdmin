package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NSLanguageRequest(

	@field:SerializedName("service_id")
	val serviceId: String? = null,

	@field:SerializedName("locale")
	val locale: String? = null
)

open class NSLanguageStringRequest (
	@SerializedName("service_id")
	@Expose
	var serviceId: String? = null,

	@SerializedName("locale")
	@Expose
	var locale: String? = null,

	@SerializedName("last_modified")
	@Expose
	var lastModified: Any? = null
)

data class NSLanguageLocaleRequest(

	@field:SerializedName("locale")
	val locale: String? = null
)

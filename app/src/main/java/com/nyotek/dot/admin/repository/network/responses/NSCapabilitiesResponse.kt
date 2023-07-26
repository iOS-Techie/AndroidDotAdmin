package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSCapabilitiesResponse(

	@field:SerializedName("data")
	val data: MutableList<CapabilitiesDataItem> = arrayListOf()
)

data class CapabilitiesDataItem(

	@field:SerializedName("is_active")
    var isActive: Boolean = false,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("label")
	val label: HashMap<String, String> = hashMapOf()
)
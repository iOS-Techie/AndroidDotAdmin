package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class ActiveInActiveFilter(

	@field:SerializedName("title")
    var title: String? = null,

	@field:SerializedName("key")
	var key: String = "",

	@field:SerializedName("is_active")
	var isActive: Boolean = false
)
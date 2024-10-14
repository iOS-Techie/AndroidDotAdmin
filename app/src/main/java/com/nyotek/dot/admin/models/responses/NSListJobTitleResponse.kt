package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class NSListJobTitleResponse(

	@field:SerializedName("data")
	val jobTitleList: MutableList<JobListDataItem>? = arrayListOf()
)

data class JobListDataItem(

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("service_id")
	val serviceId: String? = null,

	@field:SerializedName("roles")
	val roles: List<String> = arrayListOf(),

	@field:SerializedName("name")
	val name: HashMap<String, String> = hashMapOf(),

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("last_modified")
	val lastModified: String? = null
)

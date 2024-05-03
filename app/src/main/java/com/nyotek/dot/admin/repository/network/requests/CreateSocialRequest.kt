package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.SerializedName

data class CreateSocialRequest(

	@field:SerializedName("profile_pic_url")
	val profilePicUrl: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("biography")
	val biography: String? = null,

	@field:SerializedName("birth_year")
	val birthYear: Int? = null,

	@field:SerializedName("birth_month")
	val birthMonth: Int? = null,

	@field:SerializedName("birth_day")
	val birthDay: Int? = null
)

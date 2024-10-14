package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class SearchEmployeeResponse(

	@field:SerializedName("data")
    var data: MutableList<SearchEmployeeData>? = null
)

data class SearchEmployeeData(
	
	@field:SerializedName("user")
	var data: SearchEmployeeUserData? = null
)

data class SearchEmployeeUserData(
	
	@field:SerializedName("email")
	var email: String? = null,
	
	@field:SerializedName("mobile_verified")
	var mobileVerified: Boolean? = false,
	
	@field:SerializedName("email_verified")
	var emailVerified: Boolean? = false,
	
	@field:SerializedName("modified_at")
	var modifiedAt: String? = null,
	
	@field:SerializedName("user_type")
	var userType: String? = null,
	
	@field:SerializedName("username")
	var username: String? = null,
	
	@field:SerializedName("is_deleted")
	var isDeleted: Boolean = false,
	
	@field:SerializedName("id")
	var id: String? = null,
	
	@field:SerializedName("last_logged_in")
	var lastLoggedIn: String? = null,
	
	@field:SerializedName("locale")
	var locale: Boolean = false,
	
	@field:SerializedName("created_at")
	var createdAt: String? = null,
	
	@field:SerializedName("mobile")
	var mobile: String? = null
)
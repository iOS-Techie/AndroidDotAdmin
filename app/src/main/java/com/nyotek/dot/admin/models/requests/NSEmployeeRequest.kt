package com.nyotek.dot.admin.models.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NSEmployeeRequest(

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,
	@field:SerializedName("user_id")
	val userId: String? = null
)

data class NSEmployeeListRequest(
	@SerializedName("vendor_id")
	@Expose
	var vendorId: String? = null
)

data class NSAddEmployeeRequest(

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,
	@field:SerializedName("user_id")
	val userId: String? = null,
	@field:SerializedName("title_id")
	val titleId: String? = null
)

data class NSEmployeeEditRequest (

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,
	@field:SerializedName("user_id")
	val userId: String? = null,
	@field:SerializedName("title_id")
	val titleId: String? = null
)

data class NSFleetDriverRequest (

	@field:SerializedName("driver_ids")
	val driverIds: List<String>? = arrayListOf()
)

data class NSSearchEmployeeRequest(
	
	@field:SerializedName("users")
	val users: MutableList<NSSearchEmployeeData>? = null,
	
	@field:SerializedName("role_id")
	val roleId: String? = null
)

data class NSSearchEmployeeData(
	
	@field:SerializedName("mobile")
	val mobile: String? = null
)
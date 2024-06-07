package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class NSEmployeeResponse(

	@field:SerializedName("data")
	val employeeList: MutableList<EmployeeDataItem> = arrayListOf()
)

data class EmployeeDataItem(

	@field:SerializedName("is_active")
	var isActive: Boolean = false,

	@field:SerializedName("is_deleted")
	val isDeleted: Boolean = false,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("title_id")
    var titleId: String? = null,

	@field:SerializedName("last_modified")
	val lastModified: String? = null,

	@field:SerializedName("is_employee_selected")
	var isEmployeeSelected: Boolean = false,

	//Set not from API
	@field:SerializedName("vehicle_id_driver")
	var vehicleId: String? = null
)

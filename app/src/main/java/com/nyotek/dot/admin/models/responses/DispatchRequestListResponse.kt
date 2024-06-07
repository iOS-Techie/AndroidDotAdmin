package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class DispatchRequestListResponse(

	@field:SerializedName("data")
	val requestList: MutableList<DispatchRequestItem>? = arrayListOf()
)

data class DispatchRequestItem(

	@field:SerializedName("driver_id")
	val driverId: String? = null,
	@field:SerializedName("dispatch_id")
	val dispatchId: String? = null,
	@field:SerializedName("epoch")
	val epoch: Long? = null,
	@field:SerializedName("vendor_id")
	val vendorId: String? = null,
	@field:SerializedName("service_id")
	val serviceId: String? = null,
	@field:SerializedName("distance_km")
	val distanceKm: Double? = null,
	@field:SerializedName("notif_expiry")
	val notifExpiry: String? = null,
	@field:SerializedName("status")
	val status: String? = null,
	@field:SerializedName("algorithm")
	val algorithm: String? = null,
	@field:SerializedName("created_at")
	val createdAt: String? = null,
)
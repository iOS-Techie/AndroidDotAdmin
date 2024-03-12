package com.nyotek.dot.admin.repository.network.responses

import com.google.firebase.crashlytics.internal.metadata.UserMetadata
import com.google.gson.annotations.SerializedName

data class DispatchDetailResponse(

	@field:SerializedName("data")
	val data: DispatchData? = null
)

data class StatusItem(

	@field:SerializedName("ref_id")
	val refId: String? = null,

	@field:SerializedName("status_captured_time")
	val statusCapturedTime: String? = null,

	@field:SerializedName("ref_type")
	val refType: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("is_selected")
	var isSelected: Boolean? = false
)

data class DispatchData(

	@field:SerializedName("assigned_driver_id")
	val assignedDriverId: String? = null,

	@field:SerializedName("vendor_bid")
	val vendorBid: String? = null,

	@field:SerializedName("vendor_logo_url")
	val vendorLogoUrl: String? = null,

	@field:SerializedName("distance")
	val distance: Int = 0,

	@field:SerializedName("destination")
	val destination: FromToAddress? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("pickup")
	val pickup: FromToAddress? = null,

	@field:SerializedName("vendor_name")
	val vendorName: HashMap<String, String> = hashMapOf(),

	@field:SerializedName("media_url")
	val mediaUrl: HashMap<String, String> = hashMapOf(),

	@field:SerializedName("vendor_sid")
	val vendorSid: String? = null,

	@field:SerializedName("sequence")
	val sequence: String? = null,

	@field:SerializedName("zone_id")
	val zoneId: String? = null,

	@field:SerializedName("delivery_date")
	val deliveryDate: String? = null,

	@field:SerializedName("user_metadata")
	val userMetadata: UserMetadata? = null,

	@field:SerializedName("vendor_oid")
	val vendorOid: String? = null,

	@field:SerializedName("zone")
	val zone: String? = null,

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("zone_seq")
	val zoneSeq: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("modified_at")
	val modifiedAt: String? = null,

	@field:SerializedName("r_id")
	val rId: String? = null,

	@field:SerializedName("status")
	val status: List<StatusItem> = arrayListOf()
)
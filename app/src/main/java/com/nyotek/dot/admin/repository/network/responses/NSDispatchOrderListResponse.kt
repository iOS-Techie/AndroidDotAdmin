package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSDispatchOrderListResponse(
    @SerializedName("data")
    @Expose
    var orderData: MutableList<NSDispatchOrderListData>? = null
)

/**
 * The class representing the response of single order detail
 */
data class NSOrderSingleResponse(
    @SerializedName("data")
    val data: NSDispatchOrderListData? = null
)

/**
 * The class representing the order details
 */
data class NSDispatchOrderListData(
    @SerializedName("id")
    @Expose
    var dispatchId: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("r_id")
    @Expose
    var rId: String? = null,
    @SerializedName("vendor_oid")
    @Expose
    var vendorOid: String? = null,
    @SerializedName("vendor_sid")
    @Expose
    var vendorSid: String? = null,
    @SerializedName("vendor_id")
    @Expose
    var vendorId: String? = null,
    @SerializedName("assigned_driver_id")
    @Expose
    var assignedDriverId: String? = null,
    @SerializedName("status")
    @Expose
    var status: List<UserStatus> = arrayListOf(),
    @SerializedName("modified_at")
    @Expose
    var modifiedAt: String? = null,
    @SerializedName("media_url")
    @Expose
    var mediaUrl: HashMap<String, String> = hashMapOf(),
    @SerializedName("ref_type")
    @Expose
    var refType: String? = null,
    @SerializedName("pickup")
    @Expose
    var pickup: FromToAddress? = null,
    @SerializedName("destination")
    @Expose
    var destination: FromToAddress? = null,
    @SerializedName("is_delivered")
    @Expose
    var isDelivered: Boolean? = false,
    @SerializedName("user_metadata")
    @Expose
    var userMetadata: UserMetaData? = null,
    @SerializedName("vendor_branch_id")
    @Expose
    var vendorBranchId: String? = null,
    @SerializedName("vendor_bid")
    @Expose
    var vendorBid: String? = null,
    @SerializedName("vendor_payload")
    @Expose
    var vendorPayload: String? = null,
    @SerializedName("vendor_name")
    @Expose
    var vendorName: HashMap<String, String> = hashMapOf(),
    @SerializedName("vendor_logo_url")
    @Expose
    var vendorLogoUrl: String? = null,
    @SerializedName("total_items")
    @Expose
    var totalItems: Int = 0,
    @SerializedName("zone")
    @Expose
    val zone: String? = null,
    @SerializedName("zone_id")
    @Expose
    val zoneId: String? = null,
    @SerializedName("sequence")
    @Expose
    val sequence: String? = null,
    @SerializedName("delivery_date")
    @Expose
    val deliveryDate: String? = null,
    @SerializedName("zone_seq")
    @Expose
    val zoneSequence: String? = null,
    @SerializedName("distance")
    @Expose
    val distance: Int = 0
)

data class FromToAddress(
    @SerializedName("address_line")
    val addressLine: String = "",
    @SerializedName("lng")
    val lng: Double = 0.0,
    @SerializedName("ref_id")
    val refId: String = "",
    @SerializedName("lat")
    val lat: Double = 0.0
)

data class UserMetaData(
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("phone")
    val userPhone: String = "",
    @SerializedName("username")
    val userName: String = ""
)

data class UserStatus(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("status_captured_time")
    val statusCapturedTime: String = "",
    @SerializedName("ref_id")
    val refId: String = "",
    @SerializedName("ref_type")
    val refType: String = "",
)
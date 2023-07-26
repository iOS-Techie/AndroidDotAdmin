package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of otp send
 */
data class NSEditAddressRequest(
    @field:SerializedName("id")
    var id: String? = "",

    @field:SerializedName("lat")
    val lat: Double = 0.0,

    @field:SerializedName("lng")
    val lng: Double = 0.0,

    @field:SerializedName("addr1")
    var addr1: String? = null,

    @field:SerializedName("addr2")
    var addr2: String? = null,

    @field:SerializedName("city")
    var city: String? = null,

    @field:SerializedName("state")
    var state: String? = null,

    @field:SerializedName("country")
    var country: String? = null,

    @field:SerializedName("zip")
    var zip: String? = null
)
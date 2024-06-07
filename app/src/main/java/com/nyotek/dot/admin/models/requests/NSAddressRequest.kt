package com.nyotek.dot.admin.models.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of otp send
 */
data class NSAddressRequest(
    @SerializedName("address_id")
    @Expose
    var addressId: String? = ""
)
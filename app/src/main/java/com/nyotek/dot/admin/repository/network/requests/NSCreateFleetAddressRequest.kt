package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class NSCreateFleetAddressRequest(
    @SerializedName("ref_id")
    @Expose
    var refId: String? = null,

    @SerializedName("lat")
    @Expose
    var lat: Double? = null,

    @SerializedName("lng")
    @Expose
    var lng: Double? = null,

    @SerializedName("service_ids")
    @Expose
    var serviceIds: List<String>? = null,

    @SerializedName("addr1")
    @Expose
    var addr1: String? = null,

    @SerializedName("addr2")
    @Expose
    var addr2: String? = null,

    @SerializedName("city")
    @Expose
    var city: String? = null,

    @SerializedName("state")
    @Expose
    var state: String? = null,

    @SerializedName("country")
    @Expose
    var country: String? = null,

    @SerializedName("zip")
    @Expose
    var zip: String? = null
)
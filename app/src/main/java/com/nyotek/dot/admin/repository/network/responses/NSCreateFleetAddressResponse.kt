package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class NSCreateFleetAddressResponse (
    @SerializedName("data")
    @Expose
    var data: AddressData? = null
)
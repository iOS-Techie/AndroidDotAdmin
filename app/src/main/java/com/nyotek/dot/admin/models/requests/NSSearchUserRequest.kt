package com.nyotek.dot.admin.models.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of search user
 */
data class NSSearchUserRequest(
    @SerializedName("username")
    @Expose
    var username: String? = null
)

data class NSSearchMobileRequest(
    @SerializedName("mobiles")
    @Expose
    var mobiles: List<String>? = null
)
package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of otp send
 */
data class NSAppThemeRequest(
    @SerializedName("app_id")
    @Expose
    var appId: String? = ""
)
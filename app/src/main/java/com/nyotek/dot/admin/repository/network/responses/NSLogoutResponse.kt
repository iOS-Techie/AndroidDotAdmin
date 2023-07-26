package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of logout
 */
data class NSLogoutResponse(
    @SerializedName("error")
    @Expose
    var error: Error? = null
)

/**
 * The class representing the error details
 */
data class Error(
    @SerializedName("error_code")
    @Expose
    var code: String? = null
)
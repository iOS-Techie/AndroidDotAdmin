package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSUserResponse(
    @SerializedName("data")
    @Expose
    var data: NSDataUser? = null
)

data class NSDataUser(
    @SerializedName("expiresIn")
    @Expose
    var expiresIn: Long? = null,
    @SerializedName("accessToken")
    @Expose
    var accessToken: String? = null,
    @SerializedName("refreshToken")
    @Expose
    var refreshToken: String? = null
)
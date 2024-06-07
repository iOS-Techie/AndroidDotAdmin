package com.nyotek.dot.admin.models.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of refresh token
 */
data class NSRefreshTokenRequest(
    @SerializedName("refresh_token")
    @Expose
    private var refreshToken: String?
)
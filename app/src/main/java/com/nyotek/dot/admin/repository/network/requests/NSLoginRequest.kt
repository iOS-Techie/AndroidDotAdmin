package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of login
 */
data class NSLoginRequest(
    @SerializedName("email")
    @Expose
    private var email: String?,
    @SerializedName("password")
    @Expose
    private var password: String?
)
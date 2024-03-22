package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the request body of update status
 */
data class NSUpdateStatusRequest(
    @SerializedName("status")
    @Expose
    private var status: String
)
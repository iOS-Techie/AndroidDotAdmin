package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of logout
 */
data class NSCommonResponse(
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("value")
    @Expose
    var value: String? = null
)
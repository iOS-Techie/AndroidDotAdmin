package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the settings list response
 */
data class NSSettingListResponse(
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("image")
    @Expose
    var image: Int = 0
)
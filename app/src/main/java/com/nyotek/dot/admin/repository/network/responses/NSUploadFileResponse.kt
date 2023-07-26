package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of uploaded image
 */
data class NSUploadFileResponse(
    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("created")
    @Expose
    var created: String? = null,
    @SerializedName("browserUrl")
    @Expose
    var browserUrl: String? = null,
    @SerializedName("downloadUrl")
    @Expose
    var downloadUrl: String? = null
)
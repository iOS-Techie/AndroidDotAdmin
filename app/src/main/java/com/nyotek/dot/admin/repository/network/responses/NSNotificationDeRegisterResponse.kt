package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the blank response
 */
data class NSNotificationDeRegisterResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)
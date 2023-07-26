package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of user
 */
data class NSUserDetailResponse(
    @SerializedName("data")
    @Expose
    var data: NSMainDetailUser? = null
)

data class NSMainDetailUser(
    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("user_type")
    @Expose
    var userType: String? = null,
    @SerializedName("email_verified")
    @Expose
    var emailVerified: Boolean = false,
    @SerializedName("username")
    @Expose
    var username: String? = null,
    @SerializedName("last_logged_in")
    @Expose
    var lastLoggedIn: String? = null,
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    @SerializedName("is_deleted")
    @Expose
    var isDeleted: Boolean = false,
    @SerializedName("modified_at")
    @Expose
    var modifiedAt: String? = null,
    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,
    @SerializedName("mobile_verified")
    @Expose
    var mobileVerified: Boolean = false
)
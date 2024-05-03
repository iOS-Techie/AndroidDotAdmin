package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the response body of social
 */
data class NSSocialResponse(
    @SerializedName("data")
    @Expose
    var data: NSSocialDataResponse? = null
)

data class NSSocialDataResponse(
    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("profile_pic_url")
    var profilePicUrl: String? = null,

    @field:SerializedName("first_name")
    val firstName: String? = null,

    @field:SerializedName("last_name")
    val lastName: String? = null,

    @field:SerializedName("biography")
    val biography: String? = null,

    @field:SerializedName("birth_year")
    val birthYear: Int? = null,

    @field:SerializedName("birth_month")
    val birthMonth: Int? = null,

    @field:SerializedName("birth_day")
    val birthDay: Int? = null ,

    @field:SerializedName("dob")
    val dob: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("modified_at")
    val modifiedAt: String? = null
)
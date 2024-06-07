package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class NSLanguageStringResponse(

    @field:SerializedName("data")
    val data: List<LanguageDataItem> = arrayListOf()
)
data class LanguageDataItem(

    @field:SerializedName("locale")
    val locale: String? = null,

    @field:SerializedName("value")
    val value: String? = null,

    @field:SerializedName("last_modified")
    val lastModified: String? = null,

    @field:SerializedName("key")
    val key: String? = null
)

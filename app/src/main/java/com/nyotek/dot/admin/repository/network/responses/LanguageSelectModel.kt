package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LanguageSelectModel(
    @SerializedName("service_id")
    @Expose
    var serviceId: String? = null,

    @SerializedName("locale")
    @Expose
    var locale: String? = null,

    @SerializedName("label")
    @Expose
    var label: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("direction")
    @Expose
    var direction: String? = null,

    @SerializedName("last_modified")
    @Expose
    var lastModified: String? = null,
    @SerializedName("is_Selected")
    @Expose
    var isSelected: Boolean = false,
    @SerializedName("is_new")
    @Expose
    var isNew: Boolean = false
)
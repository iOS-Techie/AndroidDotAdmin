package com.nyotek.dot.admin.repository.network.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class NSCreateCompanyRequest(

    @SerializedName("name")
    @Expose
    var name: HashMap<String, String> = hashMapOf(),
    @SerializedName("service_ids")
    @Expose
    var serviceIds: MutableList<String> = arrayListOf(),
    @SerializedName("logo")
    @Expose
    var logo: String? = null,
    @SerializedName("logo_height")
    @Expose
    var logoHeight: Int? = null,
    @SerializedName("logo_width")
    @Expose
    var logoWidth: Int? = null,
    @SerializedName("logo_scale")
    @Expose
    var logoScale: String? = null,
    @SerializedName("slogan")
    @Expose
    var slogan: HashMap<String, String> = hashMapOf(),
    @SerializedName("tags")
    @Expose
    var tags: List<String> = arrayListOf(),
    @SerializedName("url")
    @Expose
    var url: String? = null,
    @SerializedName("is_active")
    @Expose
    var isActive: Boolean = false,
    @SerializedName("iso2")
    @Expose
    var iso2: String? = null
)
package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class NSCreateCompanyResponse {

        @SerializedName("data")
        @Expose
        var data: Data? = null


    class Data {
        @SerializedName("vendor_id")
        @Expose
        var vendorId: String? = null

        @SerializedName("owner_id")
        @Expose
        var ownerId: String? = null

        @SerializedName("address_id")
        @Expose
        var addressId: String? = null

        @SerializedName("name")
        @Expose
        var name: Name? = null

        @SerializedName("service_ids")
        @Expose
        var serviceIds: List<String>? = null

        @SerializedName("created")
        @Expose
        var created: String? = null

        @SerializedName("last_modified")
        @Expose
        var lastModified: String? = null

        @SerializedName("is_active")
        @Expose
        var isActive: Boolean? = null

        @SerializedName("logo")
        @Expose
        var logo: String? = null

        @SerializedName("logo_scale")
        @Expose
        var logoScale: String? = null

        @SerializedName("logo_height")
        @Expose
        var logoHeight: Int? = null

        @SerializedName("logo_width")
        @Expose
        var logoWidth: Int? = null

        @SerializedName("schedule_id")
        @Expose
        var scheduleId: String? = null

        @SerializedName("slogan")
        @Expose
        var slogan: Slogan? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("tags")
        @Expose
        var tags: List<String>? = null

        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("verified")
        @Expose
        var verified: Boolean? = null
    }

    class Name {
        @SerializedName("ar-sa")
        @Expose
        var arSa: String? = null

        @SerializedName("en-us")
        @Expose
        var enUs: String? = null
    }

    class Slogan {
        @SerializedName("ar-sa")
        @Expose
        var arSa: String? = null

        @SerializedName("en-us")
        @Expose
        var enUs: String? = null
    }
}
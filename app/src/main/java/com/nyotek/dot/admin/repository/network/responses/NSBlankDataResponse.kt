package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The class representing the blank response
 */
data class NSBlankDataResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)

data class NSCapabilitiesBlankDataResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)

data class NSEmployeeAddDeleteBlankDataResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)

data class NSEmployeeBlankDataResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)

data class NSFleetBlankDataResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)

data class NSErrorResponse(
    @SerializedName("error")
    @Expose
    var error: String? = null
)

data class NSUpdateFleetLogoResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)

data class NSVehicleBlankDataResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)

data class NSVehicleAssignBlankDataResponse(
    @SerializedName("errors")
    @Expose
    var errors: String? = null
)
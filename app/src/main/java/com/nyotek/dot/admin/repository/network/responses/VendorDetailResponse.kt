package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class VendorDetailResponse(

	@field:SerializedName("logo_scale")
	val logoScale: String? = null,

	@field:SerializedName("locales")
	val locales: HashMap<String, String>? = hashMapOf(),

	@field:SerializedName("owner_id")
	val ownerId: String? = null,

	@field:SerializedName("vendor_id")
	val vendorId: String? = null,

	@field:SerializedName("address_id")
	val addressId: String? = null,

	@field:SerializedName("name")
	val name: HashMap<String, String>? = hashMapOf(),

	@field:SerializedName("logo")
	val logo: String? = null,

	@field:SerializedName("iso2")
	val iso2: String? = null,

	@field:SerializedName("slogan")
	val slogan: HashMap<String, String>? = hashMapOf(),

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("currency_code")
	val currencyCode: String? = null,

	@field:SerializedName("exponent")
	val exponent: Int? = null
)
package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class RegionResponse(

	@field:SerializedName("data")
	val regions: MutableList<RegionDataItem>? = arrayListOf()
)

data class RegionDataItem(

	@field:SerializedName("country_code")
	val countryCode: Int? = null,

	@field:SerializedName("locales")
	val locales: HashMap<String, String>? = hashMapOf(),

	@field:SerializedName("currency_name")
	val currencyName: HashMap<String, String>? = hashMapOf(),

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("tzdata")
	val tzData: String? = null,

	@field:SerializedName("country_name")
	val countryName: HashMap<String, String>? = hashMapOf(),

	@field:SerializedName("active")
	val active: Boolean? = false,

	@field:SerializedName("iso2")
	val iso2: String? = null,

	@field:SerializedName("last_modified")
	val lastModified: String? = null,

	@field:SerializedName("currency_code")
	val currencyCode: String? = null,

	@field:SerializedName("iso3")
	val iso3: String? = null,

	@field:SerializedName("exponent")
	val exponent: Int? = null
)

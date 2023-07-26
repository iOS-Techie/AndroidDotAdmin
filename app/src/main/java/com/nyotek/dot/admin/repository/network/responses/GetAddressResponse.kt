package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class GetAddressResponse(

	@field:SerializedName("data")
	val data: AddressData? = null
)

data class AddressData(

	@field:SerializedName("coverage")
	val coverage: String? = null,

	@field:SerializedName("zip")
	var zip: String? = null,

	@field:SerializedName("ref_id")
	val refId: String? = null,

	@field:SerializedName("country")
	var country: String? = null,

	@field:SerializedName("lng")
	var lng: Double = 0.0,

	@field:SerializedName("addr2")
	var addr2: String? = null,

	@field:SerializedName("addr1")
	var addr1: String? = null,

	@field:SerializedName("city")
	var city: String? = null,

	@field:SerializedName("created")
	val created: String? = null,

	@field:SerializedName("geog")
	val geog: String? = null,

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("geom")
	val geom: String? = null,

	@field:SerializedName("contact_id")
	val contactId: String? = null,

	@field:SerializedName("searchable")
	val searchable: Boolean = false,

	@field:SerializedName("service_ids")
	val serviceIds: List<String> = arrayListOf(),

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("tag")
	val tag: String? = null,

	@field:SerializedName("state")
	var state: String? = null,

	@field:SerializedName("ref_type")
	val refType: String? = null,

	@field:SerializedName("last_modified")
	val lastModified: String? = null,

	@field:SerializedName("lat")
	var lat: Double = 0.0
)

package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class ServiceMainModel(

	@field:SerializedName("service_list")
    var serviceList: MutableList<NSGetServiceListData>? = arrayListOf(),

	@field:SerializedName("capabilities")
	var capabilities: MutableList<CapabilitiesDataItem>? = arrayListOf(),

	@field:SerializedName("fleet_list")
	var fleetDataList: MutableList<FleetData>? = arrayListOf()
)
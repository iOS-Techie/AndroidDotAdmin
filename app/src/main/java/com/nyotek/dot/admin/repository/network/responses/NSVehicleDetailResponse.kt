package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSVehicleDetailResponse(

	@field:SerializedName("data")
	val vehicleDetailData: VehicleDetailData? = null
)

data class VehicleDetailData(

	@field:SerializedName("driver_id")
	val driverId: String? = null,

	@field:SerializedName("capabilities")
	val capabilities: List<String> = arrayListOf(),

	@field:SerializedName("is_active")
	val isActive: Boolean = false,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("fleet_id")
	val fleetId: String? = null,

	@field:SerializedName("vehicle_id")
	val vehicleId: String? = null
)

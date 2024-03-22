package com.nyotek.dot.admin.repository.network.responses

import com.google.gson.annotations.SerializedName

data class NSDispatchDetailAllResponse(

	@field:SerializedName("location_history")
	val location: FleetLocationResponse? = null,
	@field:SerializedName("driver_detail")
	val driverDetail: NSDocumentListResponse? = null,
	@field:SerializedName("dispatch_detail")
	val dispatchDetail: DispatchDetailResponse? = null,
	@field:SerializedName("vendor_detail")
    var vendorDetail: VendorDetailResponse? = null,
	@field:SerializedName("driver_vehicle_detail")
	val driverVehicleDetail: NSDriverVehicleDetailResponse? = null,
	@field:SerializedName("dispatch_request")
	val dispatchRequest: DispatchRequestListResponse? = null,
	@field:SerializedName("driver_id")
	val driverId: String? = null
)
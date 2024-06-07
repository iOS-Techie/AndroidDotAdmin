package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class NSDispatchDetailAllResponse(

	@field:SerializedName("location_history")
	var location: FleetLocationResponse? = null,
	@field:SerializedName("driver_detail")
	var driverDetail: NSDocumentListResponse? = null,
	@field:SerializedName("dispatch_detail")
	var dispatchDetail: DispatchDetailResponse? = null,
	@field:SerializedName("vendor_detail")
	var vendorDetail: VendorDetailResponse? = null,
	@field:SerializedName("driver_vehicle_detail")
	var driverVehicleDetail: NSDriverVehicleDetailResponse? = null,
	@field:SerializedName("dispatch_request")
	var dispatchRequest: DispatchRequestListResponse? = null,
	@field:SerializedName("driver_list")
	var driverListModel: DriverListModel? = null,
	@field:SerializedName("driver_id")
	var driverId: String? = null
)
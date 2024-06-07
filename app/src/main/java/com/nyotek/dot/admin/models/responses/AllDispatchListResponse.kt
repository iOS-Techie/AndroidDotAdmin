package com.nyotek.dot.admin.models.responses

import com.google.gson.annotations.SerializedName

data class AllDispatchListResponse(

	@field:SerializedName("service_list")
	val serviceList: MutableList<NSGetServiceListData> = arrayListOf(),

	@field:SerializedName("dispatch_list")
	val dispatchList: MutableList<NSDispatchOrderListData> = arrayListOf()
)
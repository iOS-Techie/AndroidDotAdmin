package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.VehicleDataItem

interface NSEditVehicleCallback {

    fun editVehicle(response: VehicleDataItem, position: Int)
}
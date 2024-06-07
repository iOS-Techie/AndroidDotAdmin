package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.models.responses.VehicleDataItem


interface NSVehicleEditCallback {

    fun onVehicle(vehicleData: VehicleDataItem)
}
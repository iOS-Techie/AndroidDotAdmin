package com.nyotek.dot.admin.ui.tabs.fleets.vehicle

import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.models.responses.VehicleDataItem

object VehicleHelper {
    private var vehicleList: MutableList<VehicleDataItem>  = arrayListOf()

    fun setVehicleList(list: MutableList<VehicleDataItem>) {
        vehicleList = list
    }

    fun getVehicleList(): MutableList<VehicleDataItem> {
        return vehicleList
    }

    fun updateVehicleItem(vehicleId: String?, item: VehicleDataItem?) {
        if (vehicleList.isValidList()) {
            if (vehicleId != null && item != null) {
                val index = vehicleList.indexOfFirst { it.id == vehicleId }
                vehicleList[index] = item
            }
        }
    }
}
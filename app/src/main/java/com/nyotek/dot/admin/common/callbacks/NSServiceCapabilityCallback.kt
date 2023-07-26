package com.nyotek.dot.admin.common.callbacks

import com.nyotek.dot.admin.repository.network.responses.ServiceCapabilitiesDataItem

interface NSServiceCapabilityCallback {

    fun onDataItem(item: ServiceCapabilitiesDataItem)
}
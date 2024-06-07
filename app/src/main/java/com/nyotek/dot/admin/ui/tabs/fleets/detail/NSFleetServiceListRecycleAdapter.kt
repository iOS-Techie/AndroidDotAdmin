package com.nyotek.dot.admin.ui.tabs.fleets.detail

import android.app.Activity
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.extension.setAlphaP6
import com.nyotek.dot.admin.common.extension.setVisibility
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutServiceListHorizontalBinding
import com.nyotek.dot.admin.models.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.models.responses.FleetData
import com.nyotek.dot.admin.models.responses.NSGetServiceListData

private var itemList: MutableList<NSGetServiceListData> = arrayListOf()
private var selectedFleetData: FleetData? = null
private var selectedCompanyData: NSCreateCompanyRequest? = null
private var serviceList: MutableList<String> = arrayListOf()
private var isVendorDetailCheck: Boolean = false

class NSFleetServiceListRecycleAdapter(
    val activity: Activity, var colorResources: ColorResources, var callback: ((String, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutServiceListHorizontalBinding, NSGetServiceListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutServiceListHorizontalBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, _ ->
        with(binding) {
            response.apply {
                cbCheck.isChecked = serviceList.contains(response.serviceId)
                tvTitle.text = response.name
                colorResources.setBackground(viewDivider, colorResources.getBackgroundColor())
                colorResources.setCardBackground(viewActive, 100f, 0, if (response.isActive) colorResources.getGreenColor() else colorResources.getPrimaryColor())

                viewActive.setAlphaP6(isActive)
                tvTitle.setAlphaP6(isActive)
                viewDivider.setVisibility(position != itemList.size - 1)


                fun add(list: MutableList<String>, serviceId: String) {
                    serviceId.let { list.add(it) }
                }

                fun remove(list: MutableList<String>, serviceId: String) {
                    serviceId.let { list.add(it) }
                }

                clCheck.setOnClickListener {
                    cbCheck.isChecked = !cbCheck.isChecked
                    callback.invoke(serviceId ?: "", cbCheck.isChecked)
                }
            }
        }
    }
) {
    fun setFleetData(list: MutableList<NSGetServiceListData>, fleetData: FleetData?) {
        isVendorDetailCheck = true
        selectedFleetData = fleetData
        serviceList.clear()
        serviceList.addAll(fleetData?.serviceIds ?: arrayListOf())
        itemList = list
    }

    fun setCreateCompanyData(
        list: MutableList<NSGetServiceListData>,
        companyData: NSCreateCompanyRequest?
    ) {
        isVendorDetailCheck = false
        selectedCompanyData = companyData
        serviceList.clear()
       // serviceList.addAll(companyData?.serviceIds ?: arrayListOf())
        itemList = list
    }
}
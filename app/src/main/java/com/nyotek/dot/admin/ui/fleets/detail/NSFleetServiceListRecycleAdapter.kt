package com.nyotek.dot.admin.ui.fleets.detail

import android.app.Activity
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.callbacks.NSServiceSelectCallback
import com.nyotek.dot.admin.common.utils.*
import com.nyotek.dot.admin.databinding.LayoutServiceListHorizontalBinding
import com.nyotek.dot.admin.repository.network.requests.NSCreateCompanyRequest
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData
import com.nyotek.dot.admin.repository.network.responses.FleetData

private var itemList: MutableList<NSGetServiceListData> = arrayListOf()
private var selectedFleetData: FleetData? = null
private var selectedCompanyData: NSCreateCompanyRequest? = null
private var serviceList: MutableList<String> = arrayListOf()
private var isVendorDetailCheck: Boolean = false

class NSFleetServiceListRecycleAdapter(
    val activity: Activity, var dialog: Boolean, var callback: NSServiceSelectCallback
) : BaseViewBindingAdapter<LayoutServiceListHorizontalBinding, NSGetServiceListData>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutServiceListHorizontalBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position ->
        with(binding) {
            response.apply {
                cbCheck.isChecked = serviceList.contains(response.serviceId)
                tvTitle.text = response.name
                ColorResources.setBackground(viewDivider, ColorResources.getBackgroundColor())
                ColorResources.setCardBackground(
                    viewActive,
                    100f,
                    0,
                    if (response.isActive) ColorResources.getGreenColor() else ColorResources.getPrimaryColor()
                )

                viewActive.setAlphaP6(isActive)
                tvTitle.setAlphaP6(isActive)
                viewDivider.setVisibility(position != itemList.size - 1)


                fun checkServiceId(list: MutableList<String>) {
                    cbCheck.isChecked = list.contains(response.serviceId)
                }
                checkServiceId(
                    (if (isVendorDetailCheck) selectedFleetData?.serviceIds else selectedCompanyData?.serviceIds)
                        ?: arrayListOf()
                )

                fun add(list: MutableList<String>, serviceId: String) {
                    serviceId.let { list.add(it) }
                }

                fun remove(list: MutableList<String>, serviceId: String) {
                    serviceId.let { list.add(it) }
                }

                clCheck.setOnClickListener {
                    if (cbCheck.isChecked) {
                        cbCheck.isChecked = false
                        remove(
                            (if (dialog) selectedCompanyData?.serviceIds else selectedFleetData?.serviceIds)
                                ?: arrayListOf(), serviceId!!
                        )
                    } else {
                        cbCheck.isChecked = true
                        if (serviceId?.isNotEmpty() == true) {
                            add(
                                (if (dialog) selectedCompanyData?.serviceIds else selectedFleetData?.serviceIds)
                                    ?: arrayListOf(), serviceId
                            )
                        }
                    }
                    callback.onItemSelect(serviceId ?: "", cbCheck.isChecked)
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
        serviceList.addAll(companyData?.serviceIds ?: arrayListOf())
        itemList = list
    }
}
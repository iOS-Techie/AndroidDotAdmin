package com.nyotek.dot.admin.ui.fleets.employee

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.callbacks.NSEmployeeCallback
import com.nyotek.dot.admin.common.callbacks.NSEmployeeSwitchEnableDisableCallback
import com.nyotek.dot.admin.common.callbacks.NSVehicleSelectCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutEmployeeListBinding
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem

private var jobMap: HashMap<String, JobListDataItem> = hashMapOf()
class NSEmployeeRecycleAdapter(
    private val callback: NSEmployeeCallback,
    private val switchEnableDisableCallback: NSEmployeeSwitchEnableDisableCallback,
    private val branchItemSelect: NSVehicleSelectCallback
) : BaseViewBindingAdapter<LayoutEmployeeListBinding, EmployeeDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutEmployeeListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, _ ->
        with(binding) {
            response.apply {
                tvDescription.getMapValue(jobMap[response.titleId]?.name?: hashMapOf())
                tvEmployeeTitle.text = response.userId

                switchService.switchEnableDisable(isActive)
                ColorResources.setBackground(clEmployeeItem, if (response.isEmployeeSelected) ColorResources.getBackgroundColor() else ColorResources.getWhiteColor())

                switchService.setOnClickListener {
                    isActive = !isActive
                    switchService.switchEnableDisable(isActive)
                    switchEnableDisableCallback.switch(vendorId!!,  userId!!, isActive)
                }

                ivDelete.setOnClickListener {
                    callback.onClick(response, true)
                }

                ivEdit.setOnClickListener {
                    callback.onClick(response, false)
                }

                clEmployeeItem.setOnClickListener {
                    branchItemSelect.onItemSelect(response.vendorId?:"")
                }
            }
        }
    }
) {
    fun setJob(map: HashMap<String, JobListDataItem>) {
        jobMap = map
    }
}
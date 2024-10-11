package com.nyotek.dot.admin.ui.tabs.fleets.employee

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSDataStorePreferences
import com.nyotek.dot.admin.common.component.AppModule_ProvideDataStoreRepositoryFactory
import com.nyotek.dot.admin.common.extension.getMapValue
import com.nyotek.dot.admin.common.extension.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutEmployeeListBinding
import com.nyotek.dot.admin.models.responses.EmployeeDataItem
import com.nyotek.dot.admin.models.responses.JobListDataItem

private var roleList: MutableList<JobListDataItem> = arrayListOf()

class NSEmployeeRecycleAdapter(
    private val employeeUI: EmployeeUI,
    private val callback: ((EmployeeDataItem, Boolean, Int) -> Unit),
    private val switchCallBack: ((String, String, Boolean) -> Unit),
    private val branchItemSelect: ((String) -> Unit),
) : BaseViewBindingAdapter<LayoutEmployeeListBinding, EmployeeDataItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutEmployeeListBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, _ ->
        with(binding) {
            response.apply {
                employeeUI.setAdapter(binding, response.isEmployeeSelected, response)
                val roleName = roleList.find { it.id == response.titleId }?.name
                if (roleName.isNullOrEmpty()) {
                    tvStatus.text = "-"
                } else {
                    tvStatus.getMapValue(roleName)
                }
                
                switchService.switchEnableDisable(isActive)

                switchService.setOnClickListener {
                    isActive = !isActive
                    switchService.switchEnableDisable(isActive)
                    switchCallBack.invoke(vendorId!!,  userId!!, isActive)
                }

                ivDelete.setOnClickListener {
                    callback.invoke(response, true, position)
                }

                ivEdit.setOnClickListener {
                    callback.invoke(response, false, position)
                }

                clEmployeeItem.setOnClickListener {
                    branchItemSelect.invoke(response.userId?:"")
                }
            }
        }
    }
) {
    fun setRole(list: MutableList<JobListDataItem>) {
        roleList = list
    }
}
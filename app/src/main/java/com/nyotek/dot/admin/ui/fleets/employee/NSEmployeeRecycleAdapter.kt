package com.nyotek.dot.admin.ui.fleets.employee

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.getMapValue
import com.nyotek.dot.admin.common.utils.switchEnableDisable
import com.nyotek.dot.admin.databinding.LayoutEmployeeListBinding
import com.nyotek.dot.admin.repository.network.responses.EmployeeDataItem
import com.nyotek.dot.admin.repository.network.responses.JobListDataItem

private var jobMap: HashMap<String, JobListDataItem> = hashMapOf()
class NSEmployeeRecycleAdapter(
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
                tvDescription.getMapValue(jobMap[response.titleId]?.name?: hashMapOf())
                tvEmployeeTitle.text = response.userId

                switchService.switchEnableDisable(isActive)
                ColorResources.setBackground(clEmployeeItem, if (response.isEmployeeSelected) ColorResources.getBackgroundColor() else ColorResources.getWhiteColor())

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
                    branchItemSelect.invoke(response.vendorId?:"")
                }
            }
        }
    }
) {
    fun setJob(map: HashMap<String, JobListDataItem>) {
        jobMap = map
    }
}
package com.nyotek.dot.admin.ui.fleets.employee

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.callbacks.NSUserClickCallback
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.databinding.LayoutEmployeeSearchUserBinding
import com.nyotek.dot.admin.repository.network.responses.NSUserDetail

private var selectedId: String? = ""

class NSEmployeeUserSearchRecycleAdapter(callback: NSUserClickCallback) : BaseViewBindingAdapter<LayoutEmployeeSearchUserBinding, NSUserDetail>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutEmployeeSearchUserBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, _ ->
        with(binding) {
            response.apply {
                if (selectedId == null || response.id != selectedId ) {
                    response.isEmployeeSelected = false
                }
                ivCorrect.setVisibility(response.isEmployeeSelected)
                tvItemTitle.text = response.username

                clItemSelect.setSafeOnClickListener {
                    selectedId = response.id
                    response.isEmployeeSelected = !response.isEmployeeSelected
                    callback.onUserSelect()
                }
            }
        }
    }
)
package com.nyotek.dot.admin.ui.tabs.fleets.employee

import android.app.Activity
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.databinding.LayoutInviteUserItemBinding


class NSEmployeeUserSearchRecycleAdapter(activity: Activity, themeUI: EmployeeUI, callback: (Int) -> Unit) : BaseViewBindingAdapter<LayoutInviteUserItemBinding, String>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutInviteUserItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, _ ->
        with(binding) {
            response.apply {
                themeUI.setAddEmployeeAdapter(binding)
                
                ivDeleteEmployee.setOnClickListener {
                    callback.invoke(position)
                }
                
                layoutUser.edtValue.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        NSUtilities.showKeyboard(activity, layoutUser.edtValue)
                    } else {
                        NSUtilities.hideKeyboard(activity, layoutUser.edtValue)
                    }
                }
                
                /*clItemSelect.setSafeOnClickListener {
                    selectedId = response.id
                    response.isEmployeeSelected = !response.isEmployeeSelected
                    callback.invoke()
                }*/
            }
        }
    }
)
package com.nyotek.dot.admin.ui.settings

import android.text.InputType
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutSettingsRegisterBinding
import com.nyotek.dot.admin.repository.network.responses.NSCommonResponse

class NSSettingsUserRecycleAdapter : BaseViewBindingAdapter<LayoutSettingsRegisterBinding, NSCommonResponse>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutSettingsRegisterBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource, _ ->
        with(binding) {
            response.apply {
                layoutTitle.apply {
                    ColorResources.setCardBackground(clBorderBg, 10f, 2, ColorResources.getWhiteColor(), ColorResources.getBorderColor())
                    tvCommonTitle.text = title
                    edtValue.text = value

                    when (title) {
                        stringResource.mobile -> {
                            edtValue.inputType = InputType.TYPE_CLASS_PHONE
                        }

                        stringResource.email -> {
                            edtValue.inputType =
                                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }

                        else -> {
                            edtValue.inputType = InputType.TYPE_CLASS_TEXT
                        }
                    }
                }
            }
        }
    }
)
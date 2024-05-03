package com.nyotek.dot.admin.ui.settings

import android.view.View
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSPreferences
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.databinding.LayoutLanguageItemBinding
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel

class NSLanguageRecycleAdapter(
    private val nsPref: NSPreferences,
    private val isLanguageSelected: Boolean,
    private val onClickCallback: ((Int) -> Unit)
) : BaseViewBindingAdapter<LayoutLanguageItemBinding, LanguageSelectModel>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutLanguageItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, _ ->
        with(binding) {
            response.apply {
                ColorResources.setCardBackground(clLanguage, 8f, 2, ColorResources.getBackgroundColor(), ColorResources.getBorderColor())

                rbLanguage.apply {
                    setTextColor(ColorResources.getPrimaryColor())
                    text = label
                    rbLanguage.buttonTintList = ColorResources.getPrimaryColorState()
                    rbLanguage.layoutDirection = if (!isLanguageSelected) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
                    rbLanguage.setTextColor(ColorResources.getBlackColor())
                    rbLanguage.isChecked = nsPref.languagePosition == position

                    setSafeOnClickListener {
                        onClickCallback.invoke(position)
                    }
                }
            }
        }
    }
)
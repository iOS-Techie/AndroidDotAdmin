package com.nyotek.dot.admin.ui.settings

import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSPreferences
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.databinding.LayoutLanguageItemBinding
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel

class NSLanguageRecycleAdapter(
    private val nsPref: NSPreferences,
    private val onClickCallback: ((Int) -> Unit)
) : BaseViewBindingAdapter<LayoutLanguageItemBinding, LanguageSelectModel>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutLanguageItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position ->
        with(binding) {
            response.apply {
                ColorResources.setCardBackground(clLanguage, 8f, 2, ColorResources.getBackgroundColor(), ColorResources.getBorderColor())

                rbLanguage.apply {
                    setTextColor(ColorResources.getPrimaryColor())
                    text = response.label
                    isChecked = nsPref.languagePosition == position
                    setSafeOnClickListener {
                        onClickCallback.invoke(position)
                    }
                }
            }
        }
    }
)
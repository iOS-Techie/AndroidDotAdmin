package com.nyotek.dot.admin.ui.tabs.settings

import android.view.View
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSLanguageConfig
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutLanguageItemBinding
import com.nyotek.dot.admin.models.responses.LanguageSelectModel

class NSLanguageRecycleAdapter(
    private val colorResources: ColorResources,
    private val nsPref: NSLanguageConfig,
    private val isLanguageSelected: Boolean,
    private val onClickCallback: ((Int) -> Unit)
) : BaseViewBindingAdapter<LayoutLanguageItemBinding, LanguageSelectModel>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutLanguageItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, _ ->
        with(binding) {
            response.apply {
                colorResources.setCardBackground(clLanguage, 8f, 2, colorResources.getBackgroundColor(), colorResources.getBorderColor())

                rbLanguage.apply {
                    setTextColor(colorResources.getPrimaryColor())
                    text = label
                    rbLanguage.buttonTintList = colorResources.getPrimaryColorState()
                    rbLanguage.layoutDirection = if (!isLanguageSelected) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
                    rbLanguage.setTextColor(colorResources.getBlackColor())
                    rbLanguage.isChecked = nsPref.dataStorePreference.languagePosition == position

                    setSafeOnClickListener {
                        onClickCallback.invoke(position)
                    }
                }
            }
        }
    }
)
package com.nyotek.dot.admin.ui.common

import android.app.Activity
import android.content.Context
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.setSafeOnClickListener
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutLanguageTitleItemTextBinding
import com.nyotek.dot.admin.models.responses.LanguageSelectModel

private var itemList: MutableList<LanguageSelectModel> = arrayListOf()

class NSLanguageCommonRecycleAdapter(
    private val context: Context,
    private val colorResources: ColorResources,
    private val languageSelectCallback: ((String, Boolean, MutableList<LanguageSelectModel>) -> Unit)
) : BaseViewBindingAdapter<LayoutLanguageTitleItemTextBinding, LanguageSelectModel>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutLanguageTitleItemTextBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource, _, _ ->
        with(binding) {
            response.apply {
                stringResource.apply {
                    val colors = if (response.isSelected) colorResources.getPrimaryColor() else colorResources.getBackgroundColor()
                    colorResources.setCardDashMainBackground(
                        tvLanguageTitle,
                        3f,
                        0,
                        colors,
                        colors
                    )
                    colorResources.setCardDashMainBackground(
                        spinnerLanguageAdd,
                        3f,
                        0,
                        colors,
                        colors
                    )
                    tvLanguageTitle.setTextColor(if (response.isSelected) colorResources.getWhiteColor() else colorResources.getPrimaryColor())
                    tvLanguageTitle.text = response.locale
                    spinnerLanguageAdd.text = "+"

                    if (response.isSelected) {
                        languageSelectCallback.invoke(response.locale?:"", false, itemList)
                    }

                    fun removeSelection() {
                        for (data in itemList) {
                            data.isSelected = false
                        }
                    }

                    if (response.isNew) {
                        tvLanguageTitle.gone()
                        spinnerLanguageAdd.visible()
                        spinnerLanguageAdd.setSafeOnClickListener {
                            NSUtilities.showCreateLocalDialog((context as Activity), colorResources, itemList) {

                            }
                        }
                    } else {
                        tvLanguageTitle.visible()
                        spinnerLanguageAdd.gone()
                        tvLanguageTitle.setSafeOnClickListener {
                            removeSelection()
                            response.isSelected = true
                            languageSelectCallback.invoke(response.locale?:"", true, itemList)
                        }
                    }
                }
            }
        }
    }
) {
    fun setItem() {
        itemList = getData()
    }
}
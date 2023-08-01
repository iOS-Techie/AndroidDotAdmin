package com.nyotek.dot.admin.common

import android.app.Activity
import android.content.Context
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setSafeOnClickListener
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutLanguageTitleItemTextBinding
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel

private var itemList: MutableList<LanguageSelectModel> = arrayListOf()
class NSLanguageCommonRecycleAdapter(
    private val context: Context,
    private val languageSelectCallback: ((String, Boolean) -> Unit)
) : BaseViewBindingAdapter<LayoutLanguageTitleItemTextBinding, LanguageSelectModel>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutLanguageTitleItemTextBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, stringResource, _ ->
        with(binding) {
            response.apply {
                stringResource.apply {
                    val colors = if (response.isSelected) ColorResources.getPrimaryColor() else ColorResources.getBackgroundColor()
                    ColorResources.setCardDashMainBackground(
                        tvLanguageTitle,
                        3f,
                        0,
                        colors,
                        colors
                    )
                    ColorResources.setCardDashMainBackground(
                        spinnerLanguageAdd,
                        3f,
                        0,
                        colors,
                        colors
                    )
                    tvLanguageTitle.setTextColor(if (response.isSelected) ColorResources.getWhiteColor() else ColorResources.getPrimaryColor())
                    tvLanguageTitle.text = response.locale
                    spinnerLanguageAdd.text = "+"

                    if (response.isSelected) {
                        languageSelectCallback.invoke(response.locale?:"", false)
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
                            NSUtilities.showCreateLocalDialog((context as Activity), itemList) {

                            }
                        }
                    } else {
                        tvLanguageTitle.visible()
                        spinnerLanguageAdd.gone()
                        tvLanguageTitle.setSafeOnClickListener {
                            removeSelection()
                            response.isSelected = true
                            languageSelectCallback.invoke(response.locale?:"", true)
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
package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.callbacks.NSLanguageSelectedCallback
import com.nyotek.dot.admin.common.extension.isValidList
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.models.responses.LanguageSelectModel
import com.nyotek.dot.admin.ui.common.NSLanguageCommonRecycleAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NSCommonRecycleView : RecyclerView {
    private var languageTitleRecycleAdapter: NSLanguageCommonRecycleAdapter? = null
    var languageSelectCallback: NSLanguageSelectedCallback? = null

    @Inject
    internal lateinit var colorResources: ColorResources

    constructor(context: Context) : super(context) {
        if (!isInEditMode) {
            init(context)
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (!isInEditMode) {
            init(context)
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (!isInEditMode) {
            init(context)
        }
    }

    private fun init(context: Context) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        languageTitleRecycleAdapter =
            NSLanguageCommonRecycleAdapter(context, colorResources) { language, isNotify, list ->
                languageSelectCallback?.onItemSelect(language)
                if (isNotify) {
                    setAdapter(list, language)
                    //notifyAdapter()
                }
            }
        adapter = languageTitleRecycleAdapter
        isNestedScrollingEnabled = false
        //setLanguageTitle()
        getLocalLanguageList()
    }

    fun notifyAdapter() {
        languageTitleRecycleAdapter?.notifyDataSetChanged()
    }

    private fun getLocalLanguageList() {
        if (colorResources.themeHelper.getFleetLanguageList().isValidList()) {
            setAdapter(colorResources.themeHelper.getFleetLanguageList(), "")
        }
    }

    fun refreshAdapter() {
        val mainList: MutableList<LanguageSelectModel> = colorResources.themeHelper.getFleetLanguageList()
        val mapList = colorResources.themeHelper.getMapLocalLanguages()
        val tempList: MutableList<LanguageSelectModel> = arrayListOf()
        for ((_, value) in mapList) {
            tempList.addAll(value)
        }
        for (local in tempList) {
            if (mainList.none { it.locale!! == local.locale }) {
                mainList.add(local)
            }
        }
        setAdapterData(mainList)
    }

    private fun setAdapter(list: MutableList<LanguageSelectModel>, language: String) {
        if (list.isValidList()) {
            val updatedList = list.map { it.copy(isSelected = false) }.toMutableList()
            if (list.isValidList()) {
                if (language.isNotEmpty()) {
                    updatedList.find { it.locale == language }?.isSelected = true
                } else {
                    updatedList[0].isSelected = true
                }
            }
            setAdapterData(updatedList)
        } else {
            setAdapterData(arrayListOf())
        }
    }

    private fun setAdapterData(list: MutableList<LanguageSelectModel>) {
        languageTitleRecycleAdapter?.setData(list)
        languageTitleRecycleAdapter?.setItem()
    }
}
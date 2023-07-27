package com.nyotek.dot.admin.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.NSLanguageCommonRecycleAdapter
import com.nyotek.dot.admin.common.callbacks.NSLanguageSelectedCallback
import com.nyotek.dot.admin.common.callbacks.NSLocalLanguageCallback
import com.nyotek.dot.admin.common.utils.NSUtilities
import com.nyotek.dot.admin.common.utils.isValidList
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel
import com.nyotek.dot.admin.repository.network.responses.NSLocalLanguageResponse

class NSCommonRecycleView : RecyclerView {
    private var languageTitleRecycleAdapter: NSLanguageCommonRecycleAdapter? = null
    var languageSelectCallback: NSLanguageSelectedCallback? = null
    var selectedLanguage: String = ""
    var languageTitleList: MutableList<LanguageSelectModel> = arrayListOf()

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
            NSLanguageCommonRecycleAdapter(context, object :
                NSLanguageSelectedCallback {
                override fun onItemSelect(language: String, isNotify: Boolean) {
                    selectedLanguage = language
                    languageSelectCallback?.onItemSelect(language)
                    if (isNotify) {
                        notifyAdapter()
                    }
                }
            })
        adapter = languageTitleRecycleAdapter
        isNestedScrollingEnabled = false
        //setLanguageTitle()
        getLocalLanguageList()
    }

    fun notifyAdapter() {
        languageTitleRecycleAdapter?.notifyDataSetChanged()
    }

    private fun getLocalLanguageList() {
        if (NSApplication.getInstance().getFleetLanguageList().isValidList()) {
            setAdapter(NSApplication.getInstance().getFleetLanguageList())
        } else {
            NSUtilities.localLanguageApiCall("", object : NSLocalLanguageCallback {
                override fun onItemSelect(model: NSLocalLanguageResponse) {
                    setAdapter(model.data)
                }
            })
        }
    }

    fun refreshAdapter() {
        val mainList: MutableList<LanguageSelectModel> = NSApplication.getInstance().getFleetLanguageList()
        //val mainList: MutableList<LanguageSelectModel> = arrayListOf()
        val mapList = NSApplication.getInstance().getMapLocalLanguages()
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

    private fun setAdapter(list: MutableList<LanguageSelectModel>) {
        if (list.isValidList()) {
            selectedLanguage = list[0].locale?:""
            list[0].isSelected = true
        }
        setAdapterData(list)
    }

    private fun setAdapterData(list: MutableList<LanguageSelectModel>) {
        languageTitleRecycleAdapter?.setData(list)
        languageTitleRecycleAdapter?.setItem()
    }
}
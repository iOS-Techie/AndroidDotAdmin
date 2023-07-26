package com.nyotek.dot.admin.common

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.callbacks.NSLanguageSelectedCallback
import com.nyotek.dot.admin.common.callbacks.NSLanguageSubItemSelectCallback
import com.nyotek.dot.admin.common.utils.*
import com.nyotek.dot.admin.databinding.LayoutLanguageTitleItemTextBinding
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel


class NSLanguageCommonRecycleAdapter(
    private val context: Context,
    private val languageSelectCallback: NSLanguageSelectedCallback
) : BaseAdapter() {
    private val itemList: MutableList<LanguageSelectModel> = arrayListOf()

    fun updateData(titleList: MutableList<LanguageSelectModel>) {
        itemList.clear()
        itemList.addAll(titleList)
        notifyAdapter(this)
    }

    fun clearData() {
        itemList.clear()
        notifyAdapter(this)
    }

    fun notifyAdapter() {
        notifyAdapter(this)
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutLanguageTitleItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSViewHolder(orderView)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return itemList.size
    }

    /**
     * The view holder for order list
     *
     * @property languageBinding The order list view binding
     */
    inner class NSViewHolder(private val languageBinding: LayoutLanguageTitleItemTextBinding) :
        RecyclerView.ViewHolder(languageBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: LanguageSelectModel) {
            with(languageBinding) {
                stringResource.apply {
                    ColorResources.setCardDashMainBackground(
                        tvLanguageTitle,
                        3f,
                        0,
                        if (response.isSelected) ColorResources.getPrimaryColor() else ColorResources.getBackgroundColor(),
                        if (response.isSelected) ColorResources.getPrimaryColor() else ColorResources.getBackgroundColor()
                    )
                    ColorResources.setCardDashMainBackground(
                        spinnerLanguageAdd,
                        3f,
                        0,
                        if (response.isSelected) ColorResources.getPrimaryColor() else ColorResources.getBackgroundColor(),
                        if (response.isSelected) ColorResources.getPrimaryColor() else ColorResources.getBackgroundColor()
                    )
                    tvLanguageTitle.setTextColor(if (response.isSelected) ColorResources.getWhiteColor() else ColorResources.getPrimaryColor())
                    tvLanguageTitle.text = response.locale
                    spinnerLanguageAdd.text = "+"

                    if (response.isSelected) {
                        languageSelectCallback.onItemSelect(response.locale?:"")
                    }

                    if (response.isNew) {
                        tvLanguageTitle.gone()
                        spinnerLanguageAdd.visible()
                        spinnerLanguageAdd.setOnClickListener {
                            NSUtilities.showCreateLocalDialog((context as Activity), itemList, object : NSLanguageSubItemSelectCallback {
                                override fun onItemSelect(model: LanguageSelectModel) {

                                }

                            })
                        }
                    } else {
                        tvLanguageTitle.visible()
                        spinnerLanguageAdd.gone()
                        tvLanguageTitle.setOnClickListener {
                            removeSelection()
                            response.isSelected = true
                            languageSelectCallback.onItemSelect(response.locale?:"")
                            notifyAdapter(this@NSLanguageCommonRecycleAdapter)
                        }
                    }
                }
            }
        }
    }

    private fun removeSelection() {
        for (data in itemList) {
            data.isSelected = false
        }
    }
}
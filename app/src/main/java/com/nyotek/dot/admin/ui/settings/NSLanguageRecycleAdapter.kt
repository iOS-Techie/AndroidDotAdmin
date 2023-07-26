package com.nyotek.dot.admin.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.NSPreferences
import com.nyotek.dot.admin.common.callbacks.NSLanguageSelectCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutLanguageItemBinding
import com.nyotek.dot.admin.repository.network.responses.LanguageSelectModel

class NSLanguageRecycleAdapter(
    val languageData: MutableList<LanguageSelectModel>,
    val nsPref: NSPreferences,
    val onClickCallback: NSLanguageSelectCallback
) : BaseAdapter() {

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutLanguageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSLanguageViewHolder(orderView)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSLanguageViewHolder) {
            holder.bind(languageData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return languageData.size
    }

    /**
     * The view holder for trip history list
     *
     * @property languageBinding The trip history list view binding
     */
    inner class NSLanguageViewHolder(private val languageBinding: LayoutLanguageItemBinding) :
        RecyclerView.ViewHolder(languageBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: LanguageSelectModel) {
            with(languageBinding) {
                ColorResources.setCardBackground(clLanguage, 8f, 2, ColorResources.getBackgroundColor(), ColorResources.getBorderColor())
                rbLanguage.setTextColor(ColorResources.getPrimaryColor())
                rbLanguage.text = response.label
                rbLanguage.isChecked = nsPref.languagePosition == absoluteAdapterPosition
                rbLanguage.setOnClickListener {
                    notifyItemRangeChanged(0, languageData.size)
                    onClickCallback.onPosition(absoluteAdapterPosition)
                }
            }
        }
    }
}
package com.nyotek.dot.admin.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.callbacks.NSSettingSelectCallback
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.databinding.LayoutSettingItemBinding
import com.nyotek.dot.admin.repository.network.responses.NSSettingListResponse

class NSSettingRecycleAdapter(
    profileItemList: MutableList<NSSettingListResponse>,
    isLanguageSelected: Boolean,
    settingItemSelectCallBack: NSSettingSelectCallback
) : BaseAdapter() {
    private val profileItemListData: MutableList<NSSettingListResponse> = profileItemList
    private val nsSettingItemSelectCallBack: NSSettingSelectCallback = settingItemSelectCallBack
    private val isLanguage = isLanguageSelected

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView =
            LayoutSettingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSProfileViewHolder(orderView)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSProfileViewHolder) {
            holder.bind(profileItemListData[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return profileItemListData.size
    }

    /**
     * The view holder for order list
     *
     * @property profileBinding The order list view binding
     */
    inner class NSProfileViewHolder(private val profileBinding: LayoutSettingItemBinding) :
        RecyclerView.ViewHolder(profileBinding.root) {

        /**
         * To bind the order details view into Recycler view with given data
         *
         * @param response The order details
         */
        fun bind(response: NSSettingListResponse) {
            with(profileBinding) {
                tvProfileTitle.text = response.title
                ivSettingIc.setImageResource(response.image)
                if (absoluteAdapterPosition == itemCount - 1) {
                    viewLine.gone()
                }
                if (isLanguage) {
                    ivNext.setImageResource(R.drawable.arrow_left)
                } else {
                    ivNext.setImageResource(R.drawable.arrow_right)
                }

                clProfileItem.setOnClickListener {
                    response.title?.let { it1 -> nsSettingItemSelectCallBack.onPosition(it1) }
                }
            }
        }
    }
}
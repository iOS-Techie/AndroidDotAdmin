package com.nyotek.dot.admin.ui.settings

import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.databinding.LayoutSettingItemBinding
import com.nyotek.dot.admin.repository.network.responses.NSSettingListResponse

private var itemSize = 0

class NSSettingRecycleAdapter(
    private val isLanguageSelected: Boolean,
    private val settingItemSelectCallBack: ((String) -> Unit)
) : BaseViewBindingAdapter<LayoutSettingItemBinding, NSSettingListResponse>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutSettingItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position ->
        with(binding) {
            response.apply {
                tvProfileTitle.text = title
                ivSettingIc.setImageResource(image)

                if (position == itemSize - 1) {
                    viewLine.gone()
                }

                ivNext.setImageResource(if (isLanguageSelected) R.drawable.arrow_left else R.drawable.arrow_right)

                clProfileItem.setOnClickListener {
                    title?.let { it1 -> settingItemSelectCallBack.invoke(it1) }
                }
            }
        }
    }
) {
    fun setItemSize(size: Int) {
        itemSize = size
    }
}
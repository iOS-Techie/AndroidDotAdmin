package com.nyotek.dot.admin.ui.tabs.settings

import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.databinding.LayoutSettingItemBinding
import com.nyotek.dot.admin.models.responses.NSSettingListResponse

class NSSettingRecycleAdapter(
    private val isLanguageSelected: Boolean,
    private val settingItemSelectCallBack: ((String) -> Unit)
) : BaseViewBindingAdapter<LayoutSettingItemBinding, NSSettingListResponse>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutSettingItemBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _, position, size ->
        with(binding) {
            response.apply {
                tvProfileTitle.text = title
                ivSettingIc.setImageResource(image)

                if (position == size - 1) {
                    viewLineDivider.gone()
                }

                ivNext.setImageResource(if (isLanguageSelected) R.drawable.arrow_left else R.drawable.arrow_right)

                clProfileItem.setOnClickListener {
                    title?.let { it1 -> settingItemSelectCallBack.invoke(it1) }
                }
            }
        }
    }
)
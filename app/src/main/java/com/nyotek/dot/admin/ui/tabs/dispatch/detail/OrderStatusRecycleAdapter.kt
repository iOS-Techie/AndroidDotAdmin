package com.nyotek.dot.admin.ui.tabs.dispatch.detail

import android.view.View
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.extension.capitalizeWord
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.setImage
import com.nyotek.dot.admin.databinding.LayoutOrderStatusBinding
import com.nyotek.dot.admin.models.responses.StatusItem
import javax.inject.Inject

class OrderStatusRecycleAdapter @Inject constructor(private val themeUI: DispatchDetailUI) : BaseViewBindingAdapter<LayoutOrderStatusBinding, StatusItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutOrderStatusBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _ ,absoluteAdapterPosition, size ->
        binding.apply {
            response.apply {
                themeUI.setOrderStatusUI(binding, isSelected?:false)
                ivStatusTimeline.setImage(if(response.isSelected == true) R.drawable.ic_order_status_selected else R.drawable.ic_order_status_un_selected)
                tvStatusTitle.text = response.status?.capitalizeWord()
                tvStatusDate.text = NSDateTimeHelper.getDateForStatusView(response.statusCapturedTime)

                if (absoluteAdapterPosition == 0) {
                    view1.gone()
                } else {
                    tvStatusTitle.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                }
                if (absoluteAdapterPosition == size - 1) {
                    view2.gone()
                }
            }
        }
    }
) {

}
package com.nyotek.dot.admin.ui.dispatch.detail

import android.view.Gravity
import android.view.View
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.NSDateTimeHelper
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.capitalizeWord
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.setImage
import com.nyotek.dot.admin.databinding.LayoutOrderStatusBinding
import com.nyotek.dot.admin.repository.network.responses.StatusItem

class OrderStatusRecycleAdapter : BaseViewBindingAdapter<LayoutOrderStatusBinding, StatusItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutOrderStatusBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _ ,absoluteAdapterPosition, size ->
        binding.apply {
            response.apply {
                ivStatusTimeline.setImage(if(response.isSelected == true) R.drawable.ic_order_status_selected else R.drawable.ic_order_status_un_selected)
                tvStatusTitle.text = response.status?.capitalizeWord()
                tvStatusDate.text = NSDateTimeHelper.getDateForStatusView(response.statusCapturedTime)
                view2.setBackgroundResource(R.drawable.status_bg)

                if (response.isSelected == true) {
                    tvStatusTitle.setTextColor(ColorResources.getPrimaryColor())
                    ColorResources.setBackground(view1, ColorResources.getPrimaryColor())
                    ColorResources.setBackground(view2, ColorResources.getPrimaryColor())
                } else {
                    ColorResources.setBackground(view1, ColorResources.getTabSecondaryColor())
                    ColorResources.setBackground(view2, ColorResources.getTabSecondaryColor())
                    tvStatusTitle.alpha = 0.5f
                }

                if (absoluteAdapterPosition == 0) {
                    view1.gone()
                } else {
                    tvStatusTitle.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                    //tvStatusTitle.gravity = Gravity.CENTER or Gravity.TOP
                }
                if (absoluteAdapterPosition == size - 1) {
                    view2.gone()
                }
            }
        }
    }
) {

}
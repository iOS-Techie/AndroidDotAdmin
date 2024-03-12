package com.nyotek.dot.admin.ui.dispatch.detail

import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.base.BaseViewBindingAdapter
import com.nyotek.dot.admin.common.utils.capitalizeWord
import com.nyotek.dot.admin.common.utils.setImage
import com.nyotek.dot.admin.databinding.LayoutOrderStatusBinding
import com.nyotek.dot.admin.repository.network.responses.StatusItem

class OrderStatusRecycleAdapter : BaseViewBindingAdapter<LayoutOrderStatusBinding, StatusItem>(

    bindingInflater = { inflater, parent, attachToParent ->
        LayoutOrderStatusBinding.inflate(inflater, parent, attachToParent)
    },

    onBind = { binding, response, _ ,_ ->
        binding.apply {
            response.apply {
                ivStatusTimeline.setImage(if(response.isSelected == true) R.drawable.ic_order_status_selected else R.drawable.ic_order_status_un_selected)
                tvStatusTitle.text = response.status?.capitalizeWord()
            }
        }
    }
) {

}
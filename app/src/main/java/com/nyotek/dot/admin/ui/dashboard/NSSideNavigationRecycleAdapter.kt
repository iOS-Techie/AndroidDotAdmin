package com.nyotek.dot.admin.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.callbacks.NSSideNavigationSelectCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.common.utils.setVisibility
import com.nyotek.dot.admin.databinding.LayoutSideNavItemBinding
import com.nyotek.dot.admin.repository.network.responses.NSNavigationResponse

class NSSideNavigationRecycleAdapter(
    private val isLanguageSelected: Boolean,
    private val itemList: MutableList<NSNavigationResponse>,
    private val sideNavigationCallback: NSSideNavigationSelectCallback
) : BaseAdapter() {
    val selectedInstance = NSApplication.getInstance()

    fun updateNavigation() {
        notifyAdapter(this)
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val orderView = LayoutSideNavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSSideNavigationViewHolder(orderView)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSSideNavigationViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return itemList.size
    }

    /**
     * The view holder for side navigation list
     *
     * @property sideNavBinding The side navigation view binding
     */
    inner class NSSideNavigationViewHolder(private val sideNavBinding: LayoutSideNavItemBinding) :
        RecyclerView.ViewHolder(sideNavBinding.root) {

        /**
         * To bind the side navigation view into Recycler view with given data
         *
         * @param response The side navigation list response
         */
        fun bind(response: NSNavigationResponse) {
            with(sideNavBinding) {
                stringResource.apply {
                    tvNavSubTitle.text = user
                }
                tvNavSubTitle.text = response.title
                val isSelected = selectedInstance.getSelectedNavigationType() == response.type
                val color = if (isSelected) ColorResources.getWhiteColor() else ColorResources.getPrimaryColor()
                ivIconNav.setImageResource(response.icon)
                tvNavSubTitle.setTextColor(color)
                viewLineNav.setVisibility(!isSelected)
                if (isSelected) {
                    ColorResources.setBackground(clItem, ColorResources.getPrimaryColor())
                    ivIconNav.setColorFilter(ColorResources.getWhiteColor())
                }
                else {
                    ivIconNav.setColorFilter(ColorResources.getPrimaryColor())
                    clItem.setBackgroundResource(0)
                }

                ivSideArrow.setVisibility(isSelected)
                ivSideArrow.rotation = if (isLanguageSelected) 180f else 0f

                clItem.setOnClickListener {
                    sideNavigationCallback.onItemSelect(response,absoluteAdapterPosition)
                }
            }
        }
    }
}
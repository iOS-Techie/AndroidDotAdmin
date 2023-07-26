package com.nyotek.dot.admin.ui.settings

import android.app.Activity
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nyotek.dot.admin.common.BaseAdapter
import com.nyotek.dot.admin.common.callbacks.NSLogoSelectCallback
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.databinding.LayoutSettingsRegisterBinding
import com.nyotek.dot.admin.repository.network.responses.NSCommonResponse


class NSSettingsUserRecycleAdapter(
    val activity: Activity,
    val logoSelectCallback: NSLogoSelectCallback
) : BaseAdapter() {
    private val itemList: MutableList<NSCommonResponse> = arrayListOf()
    private var addressMap: String? = null
    private var logoUrl: String? = null

    fun updateData(userList: MutableList<NSCommonResponse>) {
        itemList.clear()
        itemList.addAll(userList)
        notifyAdapter(this)
    }

    fun updateAddress(address: String, position: Int) {
        addressMap = address
        notifyItemChanged(position)
    }

    fun updateLogoUrl(logo: String, position: Int) {
        logoUrl = logo
        notifyItemChanged(position)
    }

    fun clearData() {
        itemList.clear()
        notifyAdapter(this)
    }

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutSettingsRegisterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NSSettingsViewHolder(view)
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NSSettingsViewHolder) {
            holder.bind(itemList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCounts(): Int {
        return itemList.size
    }

    /**
     * The view holder for driver register list
     *
     * @property binding The driver register list view binding
     */
    inner class NSSettingsViewHolder(private val binding: LayoutSettingsRegisterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * To bind the register details view into Recycler view with given data
         *
         * @param response The register details
         */
        fun bind(response: NSCommonResponse) {
            with(binding) {
                layoutTitle.tvCommonTitle.text = response.title
                layoutTitle.edtValue.text = response.value
                ColorResources.setCardBackground(layoutTitle.clBorderBg, 10f, 2, ColorResources.getWhiteColor(), ColorResources.getBorderColor())

                when (response.title) {
                    stringResource.mobile -> {
                        layoutTitle.edtValue.inputType = InputType.TYPE_CLASS_PHONE
                    }
                    stringResource.email -> {
                        layoutTitle.edtValue.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    }
                    else -> {
                        layoutTitle.edtValue.inputType = InputType.TYPE_CLASS_TEXT
                    }
                }
            }
        }
    }
}
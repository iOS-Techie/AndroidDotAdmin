package com.nyotek.dot.admin.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.nyotek.dot.admin.common.extension.gone
import com.nyotek.dot.admin.common.extension.visible
import com.nyotek.dot.admin.common.utils.ColorResources
import com.nyotek.dot.admin.databinding.LayoutDispatchSpinnerItemBinding
import com.nyotek.dot.admin.databinding.LayoutDriverSpinnerItemViewBinding
import com.nyotek.dot.admin.models.responses.Properties
import javax.inject.Inject

class DriverSpinnerAdapter @Inject constructor(private val context: Context, private val items: MutableList<Properties>, private val colorResources: ColorResources) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: LayoutDispatchSpinnerItemBinding

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            binding = LayoutDispatchSpinnerItemBinding.inflate(inflater, parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as LayoutDispatchSpinnerItemBinding
        }

        val item = getItem(position) as Properties
        binding.ivBrandIcon.gone()
        binding.tvSpinnerTitle.text = item.driverId
        binding.viewBottom.gone()
        binding.ivSpinnerIco.gone()
        binding.tvSpinnerTitle.setTextColor(colorResources.getWhiteColor())

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getDropDownView(position, convertView, parent)
        val bind = LayoutDriverSpinnerItemViewBinding.bind(view)
        bind.tvSpinnerTitle.setTextColor(colorResources.getPrimaryColor())
        bind.viewBottom.visible()
        bind.ivSpinnerIco.gone()
        return view
    }
}
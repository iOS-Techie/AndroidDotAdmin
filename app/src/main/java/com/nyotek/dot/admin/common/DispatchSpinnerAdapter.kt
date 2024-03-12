package com.nyotek.dot.admin.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.common.utils.gone
import com.nyotek.dot.admin.common.utils.visible
import com.nyotek.dot.admin.databinding.LayoutDispatchSpinnerItemBinding
import com.nyotek.dot.admin.databinding.LayoutSpinnerItemDropDownBinding
import com.nyotek.dot.admin.repository.network.responses.NSGetServiceListData

class DispatchSpinnerAdapter(private val context: Context, private val items: MutableList<NSGetServiceListData>) : BaseAdapter() {

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

        val item = getItem(position) as NSGetServiceListData
        binding.ivBrandIcon.setImageResource(R.drawable.ic_hubz)
        binding.tvSpinnerTitle.text = item.name
        binding.viewBottom.gone()
        binding.ivSpinnerIco.visible()

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = super.getDropDownView(position, convertView, parent)
        val bind = LayoutDispatchSpinnerItemBinding.bind(view)
        bind.viewBottom.visible()
        bind.ivSpinnerIco.gone()
        return view
    }
}
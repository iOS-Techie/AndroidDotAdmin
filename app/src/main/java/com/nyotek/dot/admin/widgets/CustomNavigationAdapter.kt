package com.nyotek.dot.admin.widgets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.nyotek.dot.admin.R
import com.nyotek.dot.admin.databinding.LayoutSideNavItemBinding

class CustomNavigationAdapter(private val context: Context, private val items: List<Pair<Int, String>>): BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Pair<Int, String> = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_side_nav_item, parent, false)
        val bind = LayoutSideNavItemBinding.bind(view)
        val item = getItem(position)
        bind.ivIconNav.setImageResource(item.first)
        bind.tvNavSubTitle.text = item.second
        return view
    }
}
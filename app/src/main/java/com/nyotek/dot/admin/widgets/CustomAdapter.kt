package com.nyotek.dot.admin.widgets

import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.widget.TextView

class CustomAdapter(
    context: Context,
    textViewResourceId: Int,
    objects: MutableList<String>,
    private val hidingItemIndex: Int
) : ArrayAdapter<String>(
    context, textViewResourceId, android.R.id.text1, objects
) {
    /*override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        var v: View? = null
        if (position == hidingItemIndex) {
            val tv = TextView(context)
            tv.visibility = View.GONE
            v = tv
        } else {
            v = super.getDropDownView(position, null, parent)
        }
        return v!!
    }*/

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View
        if (position == hidingItemIndex) {
            val tv = TextView(context)
            tv.visibility = View.GONE
            tv.height = 0
            v = tv
        } else {
            v = super.getDropDownView(position, null, parent)
        }
        return v
    }
}
package com.nyotek.dot.admin.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var stringResource = NSApplication.getInstance().getStringModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateView(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindView(holder, position)
    }

    override fun getItemCount(): Int {
        return getItemCounts()
    }

    abstract fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun onBindView(holder: RecyclerView.ViewHolder, position: Int)

    abstract fun getItemCounts(): Int
}
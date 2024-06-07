package com.nyotek.dot.admin.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nyotek.dot.admin.common.NSUtilities
import com.nyotek.dot.admin.models.responses.StringResourceResponse
import javax.inject.Inject

open class BaseViewBindingAdapter<T : ViewBinding, D> @Inject constructor(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T,
    private val onBind: (T, D, StringResourceResponse, Int, Int) -> Unit
) : RecyclerView.Adapter<BaseViewBindingAdapter<T, D>.ViewHolder>() {
    private var data: MutableList<D> = arrayListOf()

    fun getString(): StringResourceResponse {
        return NSUtilities.getStringResource()
    }

    fun getData(): MutableList<D> {
        return data
    }

    fun clearData() {
        data.clear()
    }

    fun updateSingleData(model: D, position: Int) {
        data[position] = model
        notifyItemChanged(position)
    }

    fun setData(newData: List<D>) {
        data = newData.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        onBind(holder.binding, item, getString(), position, data.size)
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val binding: T) : RecyclerView.ViewHolder(binding.root)
}
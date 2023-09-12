package com.nyotek.dot.admin.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.nyotek.dot.admin.common.NSApplication
import com.nyotek.dot.admin.common.utils.notifyAdapter
import com.nyotek.dot.admin.repository.network.responses.StringResourceResponse

open class BaseViewBindingAdapter<T : ViewBinding, D>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T,
    private val onBind: (T, D, StringResourceResponse, Int) -> Unit
) : RecyclerView.Adapter<BaseViewBindingAdapter<T, D>.ViewHolder>() {
    var stringResource = StringResourceResponse()
    private var data: MutableList<D> = arrayListOf()

    fun getString(): StringResourceResponse {
        return stringResource
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
//        val diffCallback = ViewBindingDiffCallback(data, newData)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
        data = arrayListOf()
        data.addAll(newData.toMutableList())
        notifyAdapter(this)
        //diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        onBind(holder.binding, item, stringResource, position)
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val binding: T) : RecyclerView.ViewHolder(binding.root)
}

private class ViewBindingDiffCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
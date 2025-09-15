package com.nesher.waroongpintar.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class SimpleRecyclerAdapter<T>(
    open var mainData: MutableList<T> = mutableListOf(),
    @LayoutRes open var layoutRes: Int,
    open var listener: OnViewHolder<T>
) : RecyclerView.Adapter<SimpleRecyclerAdapter.SimpleViewHolder<T>>() {

    open class SimpleViewHolder<T>(
        itemView: View,
        val listener: OnViewHolder<T>,
        val adapterRef: SimpleRecyclerAdapter<T>?
    ) : RecyclerView.ViewHolder(itemView) {

        val layoutBinding: ViewDataBinding? = try {
            DataBindingUtil.bind(itemView)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return SimpleViewHolder(view, listener, this)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder<T>, position: Int) {
        val item = mainData[position]
        listener.onBindView(holder, item)
    }

    override fun getItemCount(): Int = mainData.size

    open fun getItemBy(t: T): T? = mainData.firstOrNull { it == t }

    open fun getItemPosition(t: T): Int = mainData.indexOf(t)

    open fun getItemAt(position: Int): T = mainData[position]

    open fun addItem(t: T) {
        addItemAt(t, mainData.size)
    }

    open fun addItemAt(t: T, index: Int) {
        mainData.add(index, t)
        notifyItemInserted(index)
    }

    open fun setItemAt(t: T, index: Int) {
        mainData[index] = t
        notifyItemChanged(index)
    }

    /**
     * Menggantikan referensi list (pass-by-value).
     * Ingat untuk memanggil notifyDataSetChanged() setelahnya jika ingin merefresh tampilan.
     */
    open fun addAllItemRelyingPassByValue(list: MutableList<T>) {
        this.mainData = list
    }

    open fun addAllItem(list: List<T>) {
        list.forEach { item ->
            if (!mainData.contains(item)) {
                val insertIndex = mainData.size
                mainData.add(item)
                notifyItemInserted(insertIndex)
            }
        }
    }

    open fun addAllItemWithIndex(position: Int, list: List<T>) {
        mainData.addAll(position, list)
        notifyItemRangeInserted(position, list.size)
    }

    open fun removeItem(t: T) {
        val position = getItemPosition(t)
        if (position >= 0) {
            mainData.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    open fun remove(list: List<T>) {
        list.forEach { mainData.remove(it) }
        notifyDataSetChanged()
    }

    open fun removeAt(position: Int) {
        if (position in mainData.indices) {
            mainData.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    open fun removeAll() {
        if (mainData.isNotEmpty()) {
            mainData.clear()
            notifyDataSetChanged()
        }
    }

    open fun updateItems(list: List<T>?) {
        if (list.isNullOrEmpty()) return
        list.forEach { item ->
            if (!mainData.contains(item)) {
                val idx = mainData.size
                mainData.add(item)
                notifyItemInserted(idx)
            }
        }
    }

    open fun updateMainData(list: MutableList<T>) {
        mainData = list
        notifyDataSetChanged()
    }

    fun interface OnViewHolder<T> {
        fun onBindView(holder: SimpleViewHolder<T>, item: T)
    }
}
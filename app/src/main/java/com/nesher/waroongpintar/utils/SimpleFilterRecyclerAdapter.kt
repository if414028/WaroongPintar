package com.nesher.waroongpintar.utils

import androidx.annotation.LayoutRes

class SimpleFilterRecyclerAdapter<T>(
    mainData: MutableList<T>,
    @LayoutRes layoutRes: Int,
    listener: SimpleRecyclerAdapter.OnViewHolder<T>,
    var onSearchListener: OnSearchListener<T>? = null
) : SimpleRecyclerAdapter<T>(mainData, layoutRes, listener) {

    var savedMainData: MutableList<T> = mainData

    constructor(
        @LayoutRes layoutRes: Int,
        listener: SimpleRecyclerAdapter.OnViewHolder<T>,
        onSearchListener: OnSearchListener<T>? = null
    ) : this(mutableListOf(), layoutRes, listener, onSearchListener)

    fun filter(text: String?) {
        if (!text.isNullOrEmpty()) {
            val filterResult = arrayListOf<T>()
            val q = text.lowercase()

            for (t in savedMainData) {
                onSearchListener?.onSearchRules(t, q)?.let { filterResult.add(it) }
            }

            if (filterResult.isEmpty()) onSearchListener?.onSearchEmpty(text)
            else onSearchListener?.onSearch(filterResult)

            mainData = filterResult.toMutableList()
        } else {
            mainData = savedMainData
        }
        notifyDataSetChanged()
    }

    override fun updateMainData(mainData: MutableList<T>) {
        savedMainData = mainData
        notifyDataSetChanged()
        super.updateMainData(mainData)
    }

    fun addItemToSavedMainData(t: T) {
        savedMainData.add(t)
        super.addItem(t)
    }

    override fun addAllItem(t: List<T>) {
        super.addAllItem(t)
        for (item in mainData) if (!savedMainData.contains(item)) savedMainData.add(item)
    }

    fun addAllItemWithLimit(t: List<T>, limit: Int) {
        mainData.clear()
        for (i in t.indices) {
            if (i > limit) break
            val item = t[i]
            if (!mainData.contains(item)) mainData.add(item)
        }
        notifyDataSetChanged()
    }

    override fun addItem(t: T) {
        super.addItem(t)
        if (!savedMainData.contains(t)) savedMainData.add(t)
    }

    override fun addItemAt(t: T, i: Int) {
        super.addItemAt(t, i)
        if (!savedMainData.contains(t)) savedMainData.add(i.coerceIn(0, savedMainData.size), t)
    }

    override fun setItemAt(t: T, i: Int) {
        super.setItemAt(t, i)
        if (i in savedMainData.indices) {
            if (!savedMainData.contains(t)) savedMainData[i] = t
        } else if (!savedMainData.contains(t)) {
            savedMainData.add(t)
        }
    }

    override fun removeItem(t: T) {
        super.removeItem(t)
        savedMainData.remove(t)
    }

    override fun removeAt(position: Int) {
        if (position in mainData.indices) {
            val item = mainData[position]
            super.removeAt(position)
            savedMainData.remove(item)
        }
    }

    override fun removeAll() {
        val snapshot = mainData.toList()
        super.removeAll()
        snapshot.forEach { savedMainData.remove(it) }
        notifyDataSetChanged()
    }

    fun addItemAtAdapter(item: T, position: Int) {
        val pos = position.coerceIn(0, mainData.size)
        mainData.add(pos, item)
        if (!savedMainData.contains(item)) {
            val savedPos = position.coerceIn(0, savedMainData.size)
            savedMainData.add(savedPos, item)
        }
        notifyItemInserted(pos)
    }

    fun removeItemAtAdapter(item: T) {
        val idx = mainData.indexOf(item)
        if (idx >= 0) {
            mainData.removeAt(idx)
            notifyItemRemoved(idx)
        }
        savedMainData.remove(item)
    }

    /**
     * Single-abstract-method supaya bisa dipakai sebagai lambda.
     * onSearch() & onSearchEmpty() diberi default (non-abstract).
     */
    fun interface OnSearchListener<T> {
        fun onSearchRules(model: T, searchedText: String): T?
        fun onSearch(models: ArrayList<T>) {}
        fun onSearchEmpty(searchedText: String) {}
    }

}
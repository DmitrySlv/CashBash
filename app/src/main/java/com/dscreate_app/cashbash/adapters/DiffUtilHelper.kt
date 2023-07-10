package com.dscreate_app.cashbash.adapters

import androidx.recyclerview.widget.DiffUtil
import com.dscreate_app.cashbash.data.models.AdModelDto

class DiffUtilHelper(
    private val oldList: List<AdModelDto>,
    private val newList: List<AdModelDto>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
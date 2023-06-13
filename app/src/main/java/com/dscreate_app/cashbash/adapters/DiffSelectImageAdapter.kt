package com.dscreate_app.cashbash.adapters

import androidx.recyclerview.widget.DiffUtil
import com.dscreate_app.cashbash.models.SelectImageItem

object DiffSelectImageAdapter: DiffUtil.ItemCallback<SelectImageItem>() {
    override fun areItemsTheSame(oldItem: SelectImageItem, newItem: SelectImageItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SelectImageItem, newItem: SelectImageItem): Boolean {
        return oldItem == newItem
    }
}
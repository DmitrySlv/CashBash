package com.dscreate_app.cashbash.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.SpListItemBinding


class DialogSpinnerAdapter: RecyclerView.Adapter<DialogSpinnerAdapter.SpViewHolder>() {

    private val mainList = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sp_list_item, parent, false)
        return SpViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    class SpViewHolder(view: View): ViewHolder(view) {
        private val binding = SpListItemBinding.bind(view)

        fun setData(text: String) = with(binding) {
            tvSpItem.text = text
        }
    }

    fun updateAdapter(list: MutableList<String>) {
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}
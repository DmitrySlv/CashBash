package com.dscreate_app.cashbash.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.SpListItemBinding


class DialogSpinnerAdapter(
    private val tvSelection: TextView,
    private val dialog: AlertDialog
    ) : RecyclerView.Adapter<DialogSpinnerAdapter.SpViewHolder>() {

    private val mainList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sp_list_item, parent, false)
        return SpViewHolder(view, tvSelection, dialog)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    class SpViewHolder(
        view: View, private val tvSelection: TextView, private val dialog: AlertDialog
    ): ViewHolder(view), OnClickListener {
        private val bindingSp = SpListItemBinding.bind(view)
        private var itemText = STRING_DEF

        fun setData(text: String) {
           bindingSp.tvSpItem.text = text
            itemText = text
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            tvSelection.text = itemText
            dialog.dismiss()
        }
    }

    fun updateAdapter(list: ArrayList<String>) {
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }

    companion object {
        private const val STRING_DEF = ""
    }
}
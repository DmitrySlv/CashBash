package com.dscreate_app.cashbash.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.AdListItemBinding


class AdsAdapter: RecyclerView.Adapter<AdsAdapter.AdHolder>() {

   private val adList = mutableListOf<AdModelDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ad_list_item, parent, false)
        return AdHolder(view)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adList[position])
    }

    override fun getItemCount(): Int {
       return adList.size
    }

    class AdHolder(view: View): ViewHolder(view) {
        private val binding = AdListItemBinding.bind(view)

        fun setData(adModel: AdModelDto) = with(binding) {
            adModel.apply {
                tvDescription.text = description
                tvPrice.text = price
            }
        }
    }

    private fun updateAdapter(newList: MutableList<AdModelDto>) {
        adList.clear()
        adList.addAll(newList)
        notifyDataSetChanged()
    }
}
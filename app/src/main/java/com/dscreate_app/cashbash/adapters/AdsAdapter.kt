package com.dscreate_app.cashbash.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.AdListItemBinding
import com.google.firebase.auth.FirebaseAuth


class AdsAdapter(private val auth: FirebaseAuth): RecyclerView.Adapter<AdsAdapter.AdHolder>() {

   private val adList = mutableListOf<AdModelDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ad_list_item, parent, false)
        return AdHolder(view, auth)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adList[position])
    }

    override fun getItemCount(): Int {
       return adList.size
    }

    class AdHolder(view: View, private val auth: FirebaseAuth): ViewHolder(view) {
        private val binding = AdListItemBinding.bind(view)

        fun setData(adModel: AdModelDto) = with(binding) {
            adModel.apply {
                tvDescription.text = description
                tvPrice.text = price
                tvTitle.text = title
            }
            showEditPanel(isOwner(adModel))
        }

        private fun isOwner(adModel: AdModelDto): Boolean {
            return adModel.uid == auth.uid
        }

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE
            }
        }
    }

    fun updateAdapter(newList: MutableList<AdModelDto>) {
        adList.clear()
        adList.addAll(newList)
        notifyDataSetChanged()
    }
}
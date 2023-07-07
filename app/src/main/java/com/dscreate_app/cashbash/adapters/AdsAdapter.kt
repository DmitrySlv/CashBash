package com.dscreate_app.cashbash.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.EditAdsActivity
import com.dscreate_app.cashbash.activities.MainActivity
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.AdListItemBinding


class AdsAdapter(private val mainAct: MainActivity): RecyclerView.Adapter<AdsAdapter.AdHolder>() {

   private val adList = mutableListOf<AdModelDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ad_list_item, parent, false)
        return AdHolder(view, mainAct)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adList[position])
    }

    override fun getItemCount(): Int {
       return adList.size
    }

    class AdHolder(view: View, private val mainAct: MainActivity): ViewHolder(view) {
        private val binding = AdListItemBinding.bind(view)

        fun setData(adModel: AdModelDto) = with(binding) {
            adModel.apply {
                tvDescription.text = description
                tvPrice.text = price
                tvTitle.text = title
            }
            showEditPanel(isOwner(adModel))

            ibEditAd.setOnClickListener(onClickEditAd(adModel))
        }

        private fun isOwner(adModel: AdModelDto): Boolean {
            return adModel.uid == mainAct.mAuth.uid
        }

        private fun showEditPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE
            }
        }

        private fun onClickEditAd(adModel: AdModelDto): OnClickListener {
            return OnClickListener {
                val editIntent = Intent(mainAct, EditAdsActivity::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.AD_MODEL_DATA, adModel)
                }
                mainAct.startActivity(editIntent)
            }
        }
    }

    fun updateAdapter(newList: MutableList<AdModelDto>) {
        adList.clear()
        adList.addAll(newList)
        notifyDataSetChanged()
    }
}
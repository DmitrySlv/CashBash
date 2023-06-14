package com.dscreate_app.cashbash.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.ImageAdapterItemBinding
import com.dscreate_app.cashbash.models.SelectImageItem

class ImageAdapter: RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    private val imageList = mutableListOf<SelectImageItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_adapter_item, parent, false)
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(imageList[position].imageUri)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ImageHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding =  ImageAdapterItemBinding.bind(view)

        fun setData(uri: String) = with(binding) {
            imView.setImageURI(Uri.parse(uri))
        }
    }

    fun updateAdapter(newList: MutableList<SelectImageItem>) {
        imageList.clear()
        imageList.addAll(newList)
        notifyDataSetChanged()
    }
}
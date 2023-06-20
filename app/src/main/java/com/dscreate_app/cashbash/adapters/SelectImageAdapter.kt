package com.dscreate_app.cashbash.adapters

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.EditAdsActivity
import com.dscreate_app.cashbash.databinding.SelectImageItemBinding
import com.dscreate_app.cashbash.utils.callbacks.AdapterCallback
import com.dscreate_app.cashbash.utils.callbacks.ItemTouchMoveCallback
import com.dscreate_app.cashbash.utils.image_picker.PixImagePicker
import com.dscreate_app.cashbash.utils.image_picker.ImageConst
import com.dscreate_app.cashbash.utils.image_picker.ImageManager

class SelectImageAdapter(
    private val callback: AdapterCallback
): RecyclerView.Adapter<SelectImageAdapter.ImageHolder>(),
    ItemTouchMoveCallback.ItemTouchListener {

    val mainList = mutableListOf<Bitmap>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_image_item, parent, false)
        return ImageHolder(view, this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount(): Int {
       return mainList.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {

        val targetItem = mainList[targetPos]
        mainList[targetPos] = mainList[startPos]
        mainList[startPos] = targetItem
        notifyItemMoved(startPos, targetPos)
    }

    override fun onClear() {
       notifyDataSetChanged()
    }

    class ImageHolder(
        view: View,
        private val adapter: SelectImageAdapter
        ): RecyclerView.ViewHolder(view) {
        private val binding = SelectImageItemBinding.bind(view)

        fun setData(bitmap: Bitmap) = with(binding) {
            if (adapterPosition < 3) {
                tvTitle.text =
                    root.context.resources.getStringArray(R.array.title_array)[adapterPosition]
            }
            ImageManager.chooseScaleType(imContent, bitmap)
            imContent.setImageBitmap(bitmap)

            imEdImage.setOnClickListener {
                PixImagePicker.getImages(
                    root.context as EditAdsActivity,
                    ImageConst.SINGLE_IMAGE,
                    ImageConst.REQUEST_CODE_GET_SINGLE_IMAGE
                )
                (root.context as EditAdsActivity).editImagePos = adapterPosition
            }
            imDelImage.setOnClickListener {
                adapter.mainList.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                for (n in 0 until adapter.mainList.size) {
                    adapter.notifyItemChanged(n)
                }
                adapter.callback.onItemDelete()
            }
        }
    }

    fun updateAdapter(newList: MutableList<Bitmap>, needClear: Boolean) {
        if (needClear) mainList.clear()
        mainList.addAll(newList)
        notifyDataSetChanged()
    }
}
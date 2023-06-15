package com.dscreate_app.cashbash.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.SelectImageItemBinding
import com.dscreate_app.cashbash.utils.callbacks.ItemTouchMoveCallback

class SelectImageAdapter: RecyclerView.Adapter<SelectImageAdapter.ImageHolder>(),
    ItemTouchMoveCallback.ItemTouchListener {

    val mainList = mutableListOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_image_item, parent, false)
        return ImageHolder(view)
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

    class ImageHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = SelectImageItemBinding.bind(view)

        fun setData(item: String) = with(binding) {
            tvTitle.text =
                root.context.resources.getStringArray(R.array.title_array)[adapterPosition]
            imContent.setImageURI(Uri.parse(item))
        }
    }

    fun updateAdapter(newList: MutableList<String>, needClear: Boolean) {
        if (needClear) mainList.clear()
        mainList.addAll(newList)
        notifyDataSetChanged()
    }
}
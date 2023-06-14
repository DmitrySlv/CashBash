package com.dscreate_app.cashbash.utils.callbacks

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchMoveCallback(private val adapter: ItemTouchAdapter): ItemTouchHelper.Callback() {

    //задается движение перемещения item
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlag, 0)
    }

    //задается фиксация item после перетаскивания в списке по позиции
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    //используется для свайпа
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    //Запускается по дефолту, но при переопределении можно изменить состояние элемента при
    // выполняемом действии. В данном случае добавл полупрозрачность при перетаскивании.
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder?.itemView?.alpha = 0.5f
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    //Запускается по дефолту, но при переопределении возвращает состояние перетаскиваемого item
    //на первоначальное. В данномслучае возвращает полупрозрачность на дефолтное до перетаскивания.
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.alpha = 1.0f
        super.clearView(recyclerView, viewHolder)
    }

    interface ItemTouchAdapter {
        fun onMove(startPos: Int, targetPos: Int)
    }
}
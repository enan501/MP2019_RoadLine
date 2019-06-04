package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import konkukSW.MP2019.roadline.R

class DateItemTouchHelperCallback(adapter: DateListAdapter, context:Context, dragDirs:Int, swipeDirs:Int) :ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs){

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    var dateListAdapter = adapter
    var context = context

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        dateListAdapter.moveItem(p1.adapterPosition, p2.adapterPosition)
        return true
    }



    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        dateListAdapter.removeItem(p0.adapterPosition)
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val icon:Bitmap
        val p = Paint()
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3

            if(dX < 0){
                p.color = Color.parseColor("#ef5f5f")
                val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                c.drawRect(background, p)
                icon = BitmapFactory.decodeResource(context.resources, R.drawable.garbage_white)
                val mutableBitmap = Bitmap.createBitmap(icon.width, icon.height, Bitmap.Config.ARGB_8888)
                val immutableDraw = BitmapDrawable(icon)


                val icon_dest = RectF(itemView.right.toFloat() - 2*width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                c.drawBitmap(mutableBitmap, null, icon_dest, p)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
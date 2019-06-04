package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.widget.TextView
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
        val builder = AlertDialog.Builder(context) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
        val deleteListDialog = //context 이용해서 레이아웃 인플레이터 생성
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.delete_list_dialog, null)
        val deleteListText = deleteListDialog.findViewById<TextView>(R.id.DL_textView)
        var deleteMessage = "일정을 지울까요?"
        deleteListText.text = deleteMessage

        builder.setView(deleteListDialog)
            .setPositiveButton("삭제") { dialogInterface, _ ->
                dateListAdapter.removeItem(p0.adapterPosition)
                dateListAdapter.notifyItemRemoved(p0.adapterPosition)
            }
            .setNegativeButton("취소") { dialogInterface, i ->
            }
            .show()

        dateListAdapter.notifyDataSetChanged()
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
            val width = height/3

            if(dX < 0){
                p.color = Color.parseColor("#ef5f5f")
                val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                c.drawRect(background, p)
//                icon = BitmapFactory.decodeResource(context.resources, R.drawable.garbage_white)
//               // val mutableBitmap = Bitmap.createBitmap(icon.width, icon.height, Bitmap.Config.ARGB_8888)
//                val immutableDraw = icon as BitmapDrawable
//                immutableDraw.setBounds(0, 0, height.toInt(), height.toInt())

                val bitmapDraw = ContextCompat.getDrawable(context,R.drawable.garbage_white) as BitmapDrawable
                val b = bitmapDraw.bitmap

                val icon_dest = RectF(itemView.right.toFloat() - 3*width, itemView.top.toFloat() + width/2, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width/2)
                //val newBitmap = immutableDraw.bitmap
                c.drawBitmap(b, null, icon_dest, p)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
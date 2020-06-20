package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.widget.TextView
import konkukSW.MP2019.roadline.R

class DateItemTouchHelperCallback(adapter: PlanListAdapter, context:Context, dragDirs:Int, swipeDirs:Int) :ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs){

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    var dateListAdapter = adapter
    var context = context

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        Log.d("mytag", "onMove : " + p1.adapterPosition.toString() + ", " + p2.adapterPosition.toString())
        dateListAdapter.moveItem(p1.adapterPosition, p2.adapterPosition)
        return true
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

}
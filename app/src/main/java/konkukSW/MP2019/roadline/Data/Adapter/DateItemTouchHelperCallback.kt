package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class DateItemTouchHelperCallback(adapter: DateListAdapter, context:Context, dragDirs:Int, swipeDirs:Int) :ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs){
    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    var dateListAdapter = adapter

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        //Log.v("planList2", dateListAdapter.items.size.toString())
        dateListAdapter.moveItem(p1.adapterPosition, p2.adapterPosition)
        return true
    }



    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        //Log.v("planList2", dateListAdapter.items.size.toString())
        dateListAdapter.removeItem(p0.adapterPosition)
    }
}
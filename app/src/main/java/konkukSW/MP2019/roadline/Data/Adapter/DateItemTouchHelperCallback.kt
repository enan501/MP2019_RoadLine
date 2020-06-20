package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class DateItemTouchHelperCallback(adapter: PlanListAdapter, context:Context, dragDirs:Int, swipeDirs:Int) :ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs){

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    private val POSITION_UNKNOWN = -1
    private var oldPosition = POSITION_UNKNOWN
    private var newPosition = POSITION_UNKNOWN
    var dateListAdapter = adapter
    var context = context

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        Log.d("mytag", "onMove : " + p1.adapterPosition.toString() + ", " + p2.adapterPosition.toString())
        oldPosition = p1.adapterPosition
        if ( oldPosition > POSITION_UNKNOWN && newPosition != p2.adapterPosition){
            newPosition = p2.adapterPosition
            return true
        }
        return false
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        Log.d("mytag","clear")
        dateListAdapter.onAttachedToRecyclerView(recyclerView)
        dateListAdapter.notifyDataSetChanged()
        oldPosition = POSITION_UNKNOWN;
        newPosition = POSITION_UNKNOWN;
        super.clearView(recyclerView, viewHolder)
    }

    override fun onMoved(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            fromPos: Int,
            target: RecyclerView.ViewHolder,
            toPos: Int,
            x: Int,
            y: Int
    ) {
        dateListAdapter.moveItem(oldPosition,newPosition)
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

}
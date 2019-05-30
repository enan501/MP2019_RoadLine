package konkukSW.MP2019.roadline.Data.Adapter

import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.Spot
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.Fragment1

class DateListAdapter(val items:ArrayList<Spot>, val listener: ItemDragListener): RecyclerView.Adapter<DateListAdapter.ViewHolder>()  {

    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view:View, data: Spot, position: Int)
    }

    var itemClickListener :OnItemClickListener? = null

    interface ItemDragListener{
        fun onStartDrag(holder: RecyclerView.ViewHolder)
    }

    fun moveItem(pos1:Int, pos2:Int){ //객체 두개 바꾸기 함수
        val item1 = items.get(pos1)
        items.removeAt(pos1)
        items.add(pos2, item1)
        notifyItemMoved(pos1, pos2)
    }

    fun removeItem(pos:Int){ // 객체 지우기 함수
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_spot, p0, false)
        return  ViewHolder(v, listener)
    }

    override fun getItemCount(): Int {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) { //viewHolder의 내용 초기화
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        p0.spotName.text = items.get(p1).spot.toString()

    }

    inner class ViewHolder(itemView: View, listener: ItemDragListener) : RecyclerView.ViewHolder(itemView) { //데이터 저장 구조
        var spotName: TextView
        var dragBtn: ImageView

        init {
            spotName = itemView.findViewById(R.id.rs_spotName)
            dragBtn = itemView.findViewById(R.id.rs_dragBtn)
            dragBtn.setOnTouchListener { v, event ->
                if(event.action == MotionEvent.ACTION_DOWN){
                    listener.onStartDrag(this)
                }
                false
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }

        }

}}
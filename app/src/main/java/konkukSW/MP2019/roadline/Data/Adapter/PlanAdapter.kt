package konkukSW.MP2019.roadline.Data.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R

//val VIEW_TYPE_0 = 0
//val VIEW_TYPE_1 = 1
//val VIEW_TYPE_2 = 2
//val VIEW_TYPE_3 = 3
//val VIEW_TYPE_4 = 4
//val VIEW_TYPE_5 = 5
//val VIEW_TYPE_6 = 6
//val VIEW_TYPE_7 = 7
//val VIEW_TYPE_8 = 8
//val VIEW_TYPE_9 = 9
//val VIEW_TYPE_10 = 10

class PlanAdapter(val items:ArrayList<Plan>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
//    fun moveItem(pos1:Int, pos2:Int)
//    {
////        val item1 = items.get(pos1)
////        items.removeAt(pos1)
////        items.add(pos2, item1)
////        notifyItemMoved(pos1, pos2)
//    }
//    fun removeItem(pos:Int)
//    {
//        items.removeAt(pos)
//        notifyItemRemoved(pos)
//    }
//    private val TYPE_ITEM = 0
//    private val TYPE_EMPTY = 1

    interface OnItemClickListener {
        fun OnItemClick(holder: ItemViewHolder, view: View, data: Plan, position: Int)
    }
    var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_plan, p0, false)
        return ItemViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if(holder is ItemViewHolder){
            Log.d("mytag", position.toString() + " : " + items[position].toString())
            if(items[position].humanFlag)
                holder.humanImg.visibility = View.VISIBLE
            else
                holder.humanImg.visibility = View.INVISIBLE
            holder.name.text = items[position].name

            when(items[position].viewType){
                0->{
                    holder.roadImg.setImageResource(R.drawable.road_midle)
                }
                1->{
                    holder.roadImg.setImageResource(R.drawable.road_right_end)
                }
                2->{
                    holder.roadImg.setImageResource(R.drawable.road_left_end)
                }
                3->{
                    holder.roadImg.setImageResource(R.drawable.road_right_down_end)
                }
                4->{
                    holder.roadImg.setImageResource(R.drawable.road_left_down_end)
                }
                5->{
                    holder.roadImg.setImageResource(R.drawable.road_right_down)
                }
                6->{
                    holder.roadImg.setImageResource(R.drawable.roadl_right_up)
                }
                7->{
                    holder.roadImg.setImageResource(R.drawable.road_left_down)
                }
                8->{
                    holder.roadImg.setImageResource(R.drawable.road_left_up)
                }
                9->{ //empty item
                    holder.humanImg.visibility = View.GONE
                    holder.roadImg.visibility = View.GONE
                    holder.name.visibility = View.GONE
                }
                10->{
                    holder.roadImg.setImageResource(R.drawable.plan_one)
                }
            }

        }

//        if(holder is EmptyViewHolder){
//
//        }

//        if (holder is ViewHolder0) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder1) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder2) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder3) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder4) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder5) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder6) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder7) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder8) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
//        if (holder is ViewHolder10) // 이미지 아이템
//        {
//            if(items.get(position).humanFlag == true)
//                holder.humanImg.visibility = View.VISIBLE
//            else
//                holder.humanImg.visibility = View.GONE
//            holder.name.text = items.get(position).name.toString()
//        }
    }

    inner class ItemViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var humanImg : ImageView
        var roadImg : ImageView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            humanImg = itemView.findViewById(R.id.man_img)
            roadImg = itemView.findViewById(R.id.roadImageView)
            itemView.setOnClickListener {
                val position = adapterPosition
                if(items[position].viewType != 9){
                    itemClickListener?.OnItemClick(this, it, items[position], position)
                }
            }

        }
    }

//    inner class EmptyViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        init{
//        }
//    }

//    inner class ViewHolder0(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//
//    }
//    inner class ViewHolder1(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    inner class ViewHolder2(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    inner class ViewHolder3(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    inner class ViewHolder4(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    inner class ViewHolder5(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    inner class ViewHolder6(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    inner class ViewHolder7(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    inner class ViewHolder8(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    inner class ViewHolder9(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        init{
//
//        }
//    }
//    inner class ViewHolder10(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//        var name: TextView
//        var humanImg : ImageView
//        init{
//            name = itemView.findViewById(R.id.plan_Item_textView)
//            humanImg = itemView.findViewById(R.id.man_img)
//            itemView.setOnClickListener {
//                val position = adapterPosition
//                itemClickListener?.OnItemClick(this, it, items[position], position)
//            }
//        }
//    }
//    override fun getItemViewType(position: Int): Int {
//        return if(items[position].viewType === 9){
//            TYPE_EMPTY
//        }
//        else{
//            TYPE_ITEM
//        }
//        return if (items.get(position).viewType === 0) {
//            VIEW_TYPE_0
//        }
//        else if (items.get(position).viewType === 1) {
//            VIEW_TYPE_1
//        }
//        else if (items.get(position).viewType === 2) {
//            VIEW_TYPE_2
//        }
//        else if (items.get(position).viewType === 3) {
//            VIEW_TYPE_3
//        }
//        else if (items.get(position).viewType === 4) {
//            VIEW_TYPE_4
//        }
//        else if (items.get(position).viewType === 5) {
//            VIEW_TYPE_5
//        }
//        else if (items.get(position).viewType === 6) {
//            VIEW_TYPE_6
//        }
//        else if (items.get(position).viewType === 7) {
//            VIEW_TYPE_7
//        }
//        else if (items.get(position).viewType === 8) {
//            VIEW_TYPE_8
//        }
//        else if (items.get(position).viewType === 9) {
//            VIEW_TYPE_9
//        }
//        else {
//            VIEW_TYPE_10
//        }
//    }
}

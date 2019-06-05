package konkukSW.MP2019.roadline.Data.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R

val VIEW_TYPE_0 = 0
val VIEW_TYPE_1 = 1
val VIEW_TYPE_2 = 2
val VIEW_TYPE_3 = 3
val VIEW_TYPE_4 = 4
val VIEW_TYPE_5 = 5
val VIEW_TYPE_6 = 6
val VIEW_TYPE_7 = 7
val VIEW_TYPE_8 = 8
val VIEW_TYPE_9 = 9
val VIEW_TYPE_10 = 10

class PlanAdapter(val items:ArrayList<Plan>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun moveItem(pos1:Int, pos2:Int)
    {
//        val item1 = items.get(pos1)
//        items.removeAt(pos1)
//        items.add(pos2, item1)
//        notifyItemMoved(pos1, pos2)
    }
    fun removeItem(pos:Int)
    {
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }
    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder0, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder1, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder2, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder3, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder4, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder5, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder6, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder7, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder8, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder9, view: View, data: Plan, position: Int)
        fun OnItemClick(holder: ViewHolder10, view: View, data: Plan, position: Int)
    }
    var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (p1 === 0) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_middle_layout, p0, false)
            return ViewHolder0(v)
        }
        else if (p1 === 1) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_right_end_layout, p0, false)
            return ViewHolder1(v)
        }
        else if (p1 === 2) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_left_end_layout, p0, false)
            return ViewHolder2(v)
        }
        else if (p1 === 3) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_right_down_end_layout, p0, false)
            return ViewHolder3(v)
        }
        else if (p1 === 4) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_left_down_end_layout, p0, false)
            return ViewHolder4(v)
        }
        else if (p1 === 5) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_right_down_layout, p0, false)
            return ViewHolder5(v)
        }
        else if (p1 === 6) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_right_up_layout, p0, false)
            return ViewHolder6(v)
        }
        else if (p1 === 7) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_left_down_layout, p0, false)
            return ViewHolder7(v)
        }
        else if (p1 === 8) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_left_up_layout, p0, false)
            return ViewHolder8(v)
        }
        else if (p1 === 9) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_empty_layout, p0, false)
            return ViewHolder9(v)
        }
        else{
            val v = LayoutInflater.from(p0.context).inflate(R.layout.plan_one_layout, p0, false)
            return ViewHolder10(v)
        }
    }

    override fun getItemCount(): Int {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        if (holder is ViewHolder0) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder1) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder2) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder3) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder4) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder5) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder6) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder7) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder8) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }
        if (holder is ViewHolder10) // 이미지 아이템
        {
            holder.name.text = items.get(position).name.toString()
        }

    }

    inner class ViewHolder0(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }

    }
    inner class ViewHolder1(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    inner class ViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    inner class ViewHolder3(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    inner class ViewHolder4(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    inner class ViewHolder5(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    inner class ViewHolder6(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    inner class ViewHolder7(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    inner class ViewHolder8(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    inner class ViewHolder9(itemView: View): RecyclerView.ViewHolder(itemView) {
        init{

        }
    }
    inner class ViewHolder10(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name: TextView
        init{
            name = itemView.findViewById(R.id.plan_Item_textView)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (items.get(position).viewType === 0) {
            VIEW_TYPE_0
        }
        else if (items.get(position).viewType === 1) {
            VIEW_TYPE_1
        }
        else if (items.get(position).viewType === 2) {
            VIEW_TYPE_2
        }
        else if (items.get(position).viewType === 3) {
            VIEW_TYPE_3
        }
        else if (items.get(position).viewType === 4) {
            VIEW_TYPE_4
        }
        else if (items.get(position).viewType === 5) {
            VIEW_TYPE_5
        }
        else if (items.get(position).viewType === 6) {
            VIEW_TYPE_6
        }
        else if (items.get(position).viewType === 7) {
            VIEW_TYPE_7
        }
        else if (items.get(position).viewType === 8) {
            VIEW_TYPE_8
        }
        else if (items.get(position).viewType === 9) {
            VIEW_TYPE_9
        }
        else {
            VIEW_TYPE_10
        }
    }
}

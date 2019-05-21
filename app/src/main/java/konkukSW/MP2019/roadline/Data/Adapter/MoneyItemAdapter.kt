package konkukSW.MP2019.roadline.Data.Adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import java.nio.file.Files.size
import android.text.method.TextKeyListener.clear
import android.widget.*


val VIEW_TYPE_A = 0
val VIEW_TYPE_B = 1
val VIEW_TYPE_C = 2
val VIEW_TYPE_D = 3
val VIEW_TYPE_E = 4

class MoneyItemAdapter(val items:ArrayList<MoneyItem>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemLongClickListener{
        fun OnItemLongClick(holder:ViewHolder1, view:View, data:MoneyItem, position: Int )
    }
    var itemLongClickListener : OnItemLongClickListener? = null

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
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (p1 === 0) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_day_layout, p0, false)
            return ViewHolder0(v)
        } else if (p1 === 1) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_item_layout, p0, false)
            return ViewHolder1(v)
        }
        else if (p1 === 2) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_empty_layout, p0, false)
            return ViewHolder2(v)
        }
        else if (p1 === 3) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_total_layout, p0, false)
            return ViewHolder3(v)
        }
        else{
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_add_layout, p0, false)
            return ViewHolder4(v)
        }
    }

    override fun getItemCount(): Int {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        if (holder is ViewHolder1)
        {
            holder.price.text = items.get(position).price
            if(items.get(position).cate == 1)
                holder.img.setImageResource(R.drawable.testimg1)
            else
                holder.img.setImageResource(R.drawable.testimg2)
        }
        else if (holder is ViewHolder3)
            holder.totalPrice.text = items.get(position).price

    }

    inner class ViewHolder0(itemView: View): RecyclerView.ViewHolder(itemView) {
        init{
        }
    }
    inner class ViewHolder1(itemView: View): RecyclerView.ViewHolder(itemView) {
        var price: TextView
        var img: ImageView
        init{
            price = itemView.findViewById(R.id.money_Item_textView)
            img = itemView.findViewById(R.id.money_Item_imageView)

            /* 리사이클뷰 어댑터에 리스너 달기 */
            itemView.setOnLongClickListener{
                val position = adapterPosition
                itemLongClickListener?.OnItemLongClick(this, it, items[position], position)
                true
            }
        }


    }
    inner class ViewHolder2(itemView: View): RecyclerView.ViewHolder(itemView) {
        init{

        }
    }
    inner class ViewHolder3(itemView: View): RecyclerView.ViewHolder(itemView) {
        var totalPrice: TextView

        init{
            totalPrice = itemView.findViewById(R.id.money_Item_textView2)
        }
    }
    inner class ViewHolder4(itemView: View): RecyclerView.ViewHolder(itemView) {
        init{

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.get(position).viewType === 0) {
            VIEW_TYPE_A
        } else if (items.get(position).viewType === 1) {
            VIEW_TYPE_B
        }
        else if (items.get(position).viewType === 2) {
            VIEW_TYPE_C
        }
        else if (items.get(position).viewType === 3) {
            VIEW_TYPE_D
        }
        else {
            VIEW_TYPE_E
        }
    }
    fun onLongClick(view: View): Boolean {
        // Handle long click
        // Return true to indicate the click was handled
        return true
    }
}

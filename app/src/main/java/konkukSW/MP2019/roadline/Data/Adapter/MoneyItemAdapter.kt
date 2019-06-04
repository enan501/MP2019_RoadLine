package konkukSW.MP2019.roadline.Data.Adapter

import android.graphics.BitmapFactory
import android.net.Uri
import android.support.annotation.IntegerRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import android.widget.*

var dayCount = 2; // 이건 디비로 나중에 뽑아와야함.

val VIEW_TYPE_A = 0
val VIEW_TYPE_B = 1
val VIEW_TYPE_C = 2
val VIEW_TYPE_D = 3
val VIEW_TYPE_E = 4
val VIEW_TYPE_F = 5

class MoneyItemAdapter(val items: ArrayList<MoneyItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemLongClickListener {
        fun OnItemLongClick(holder: ViewHolder1, view: View, data: MoneyItem, position: Int)
    }

    var itemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder4, view: View, data: MoneyItem, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener2 {
        fun OnItemClick2(holder: ViewHolder1, view: View, data: MoneyItem, position: Int)
    }

    var itemClickListener2: OnItemClickListener2? = null

    fun moveItem(pos1: Int, pos2: Int) {
//        val item1 = items.get(pos1)
//        items.removeAt(pos1)
//        items.add(pos2, item1)
//        notifyItemMoved(pos1, pos2)
    }

    fun removeItem(pos: Int) {
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
        } else if (p1 === 2) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_empty_layout, p0, false)
            return ViewHolder2(v)
        } else if (p1 === 3) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_total_layout, p0, false)
            return ViewHolder3(v)
        } else if (p1 === 4) {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_add_layout, p0, false)
            return ViewHolder4(v)
        } else {
            val v = LayoutInflater.from(p0.context).inflate(R.layout.money_firstempty_layout, p0, false)
            return ViewHolder5(v)
        }
    }

    override fun getItemCount(): Int {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        if (holder is ViewHolder1) // 이미지 아이템
        {
            if(items.get(position).price == -5) // 추억함 아이템일 경우
            {
                holder.price.visibility = View.GONE
                holder.cover.visibility = View.GONE
            }
            holder.price.text = items.get(position).price.toString() + " " + items.get(position).symbol
            if (items.get(position).img == "") {
                when (items.get(position).cate) {
                    "식사" -> holder.img.setImageResource(R.drawable.meal)
                    "쇼핑" -> holder.img.setImageResource(R.drawable.shopping)
                    "교통" -> holder.img.setImageResource(R.drawable.transport)
                    "관광" -> holder.img.setImageResource(R.drawable.tour)
                    "숙박" -> holder.img.setImageResource(R.drawable.lodgment)
                    "기타" -> holder.img.setImageResource(R.drawable.etc)
                }
            } else
                holder.img.setImageBitmap(BitmapFactory.decodeFile(items.get(position).img))
        } else if (holder is ViewHolder0) { // 데이 아이템
            holder.day.text = "DAY" + items.get(position).dayNum.toString()
            holder.date.text = items.get(position).date
        } else if (holder is ViewHolder3) { // 토탈 아이템
            holder.totalPrice.text = items.get(position).price.toString() + " " +items.get(position).symbol
        }
    }

    inner class ViewHolder0(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var day: TextView
        var date: TextView

        init {
            day = itemView.findViewById(R.id.money_Item_dayTextView)
            date = itemView.findViewById(R.id.money_Item_dateTextView)

        }
    }

    inner class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var price: TextView
        var img: ImageView
        var cover: ImageView
        init {
            price = itemView.findViewById(R.id.money_Item_textView)
            img = itemView.findViewById(R.id.money_Item_imageView)
            cover = itemView.findViewById(R.id.img_cover)
            /* 리사이클뷰 어댑터에 리스너 달기 */
            itemView.setOnLongClickListener {
                val position = adapterPosition
                itemLongClickListener?.OnItemLongClick(this, it, items[position], position)
                true
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener2?.OnItemClick2(this, it, items[position], position)
            }
        }


    }

    inner class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {

        }
    }

    inner class ViewHolder3(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var totalPrice: TextView

        init {
            totalPrice = itemView.findViewById(R.id.money_Item_total)
        }
    }

    inner class ViewHolder4(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            /* 리사이클뷰 어댑터에 리스너 달기 */
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }
    }

    inner class ViewHolder5(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.get(position).viewType === 0) {
            VIEW_TYPE_A
        } else if (items.get(position).viewType === 1) {
            VIEW_TYPE_B
        } else if (items.get(position).viewType === 2) {
            VIEW_TYPE_C
        } else if (items.get(position).viewType === 3) {
            VIEW_TYPE_D
        } else if (items.get(position).viewType === 4) {
            VIEW_TYPE_E
        } else {
            VIEW_TYPE_F
        }
    }
}

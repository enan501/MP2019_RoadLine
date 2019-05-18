package konkukSW.MP2019.roadline.Data.Adapter

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R

class MoneyItemAdapter(val items:ArrayList<MoneyItem>)
    : RecyclerView.Adapter<MoneyItemAdapter.ViewHolder>() {

    /* 리사이클뷰 어댑터에 리스너 달기 */
    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view: View, data:MoneyItem, position: Int )
    }
    var itemClickListener : OnItemClickListener? = null

    fun moveItem(pos1:Int, pos2:Int)
    {
        val item1 = items.get(pos1)
        items.removeAt(pos1)
        items.add(pos2, item1)
        notifyItemMoved(pos1, pos2)
    }
    fun removeItem(pos:Int)
    {
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val v = LayoutInflater.from(p0.context).inflate(R.layout.money_item_layout, p0, false) // row의 인스턴스 만듬
        return ViewHolder(v) // ViewHolder로 생선한거 전달
    }

    override fun getItemCount(): Int {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) { // p1: 포지션 정보
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        p0.price.text = items.get(p1).price.toString()

        if(items.get(p1).headFlag == 0) {
            p0.layout1.visibility = View.GONE
            p0.layout3.visibility = View.GONE
        }
        else if(items.get(p1).headFlag == 1) {
            p0.layout1.visibility = View.GONE
            p0.layout2.visibility = View.GONE
        }
        else if(items.get(p1).headFlag == 2) {
            p0.layout2.visibility = View.GONE
            p0.layout3.visibility = View.GONE
        }
        if(items.get(p1).cate == 1)
            p0.img.setImageResource(R.drawable.testimg1)
        else
            p0.img.setImageResource(R.drawable.testimg2)

    }

    inner class ViewHolder(itemView: View) // 레이아웃에 있는 위젯들 연결해주는 역할
        : RecyclerView.ViewHolder(itemView)
    {
        var price: TextView
        var img: ImageView
        var layout1: ConstraintLayout
        var layout2: LinearLayout
        var layout3: LinearLayout


        init{
            price = itemView.findViewById(R.id.money_Item_textView)
            img = itemView.findViewById(R.id.money_Item_imageView)
            layout1 = itemView.findViewById(R.id.money_layout1)
            layout2 = itemView.findViewById(R.id.money_layout2)
            layout3 = itemView.findViewById(R.id.money_layout3)

        }
    }

}
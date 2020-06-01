package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import java.text.DecimalFormat

class MoneyListAdapter(val context:Context, val items: ArrayList<T_Day>, val isAll: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onButtonClick(holder: ViewHolder, view: View, data: T_Day, position: Int)
        fun onItemClick(data: T_Money)
    }

    lateinit var itemClickListener: OnItemClickListener
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayNumText:TextView
        var dateText:TextView
        var addButton:ImageView
        var rView:RecyclerView
        var totalText:TextView

        init {
            dayNumText = itemView.findViewById(R.id.dayNumText)
            dateText = itemView.findViewById(R.id.dateText)
            addButton = itemView.findViewById(R.id.addButton)
            rView = itemView.findViewById(R.id.rView)
            totalText = itemView.findViewById(R.id.totalText)
            addButton.setOnClickListener {
                itemClickListener.onButtonClick(this, it, items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_money_day, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if(p0 is ViewHolder){
            p0.dayNumText.text = "DAY" + items.get(p1).num.toString()
            p0.dateText.text = items.get(p1).date
            val moneyItems = items.get(p1).moneyList
            val moneyAdapter = MoneyGridAdapter(context, moneyItems)
            moneyAdapter.itemClickListener = object :MoneyGridAdapter.OnItemClickListener{
                override fun onItemClick(
                        holder: MoneyGridAdapter.ViewHolder, view: View, data: T_Money, position: Int) {
                    itemClickListener.onItemClick(data)
                }
            }
            p0.rView.adapter = moneyAdapter
            val animator = p0.rView.itemAnimator
            if(animator is SimpleItemAnimator){
                animator.supportsChangeAnimations = false
            }
            p0.rView.layoutManager = GridLayoutManager(context, 3)
            var totalKorPrice = 0.0
            for(i in moneyItems){
                totalKorPrice += i.price
            }
            if(isAll){
                if(totalKorPrice.toString().length >= 6){
                    p0.totalText.text = shortFormat.format(totalKorPrice) + "₩"
                }
                else{
                    p0.totalText.text = longFormat.format(totalKorPrice) + "₩"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


}
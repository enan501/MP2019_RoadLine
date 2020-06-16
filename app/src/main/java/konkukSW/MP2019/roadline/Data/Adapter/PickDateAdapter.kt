package konkukSW.MP2019.roadline.Data.Adapter

import android.location.Location
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.PickDate
import konkukSW.MP2019.roadline.R
import org.threeten.bp.format.DateTimeFormatter
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class PickDateAdapter(var items:ArrayList<PickDate>): RecyclerView.Adapter<PickDateAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_pick_date,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            val dateFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val date = LocalDate.ofEpochDay(items[p1].date)
            p0.date.text = date.format(dateFormat)
        }
        else{
            val dateForamt = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val date = org.threeten.bp.LocalDate.ofEpochDay(items[p1].date)
            p0.date.text = date.format(dateForamt)
        }
        if(items.get(p1).day > 0){ //일반 날짜
            p0.itemView.visibility =View.VISIBLE
            p0.day.text = "Day " + items.get(p1).day.toString()
            p0.date.visibility = View.VISIBLE
        }
        else if(items.get(p1).day == -1) //맨 마지막 추가버튼
        {
            p0.itemView.visibility =View.VISIBLE
            p0.day.text = "+"
            p0.day.textSize = 30.0f
            p0.date.visibility = View.INVISIBLE
        }
        else{ //양쪽 끝
            p0.itemView.visibility =View.INVISIBLE
        }
    }
    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, data: PickDate, position: Int)
    }
    var itemClickListener : OnItemClickListener? = null
    inner class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        var day: TextView
        var date: TextView
        init{
            day = itemView.findViewById(R.id.PDItem_day)
            date = itemView.findViewById(R.id.PDItem_date)
            itemView.setOnClickListener{
                val position = adapterPosition
                if(position != 0 && position != items.size-1)
                    itemClickListener?.OnItemClick(this,items[position],position)
            }
        }
    }
}

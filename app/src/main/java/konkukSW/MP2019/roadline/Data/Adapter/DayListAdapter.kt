package konkukSW.MP2019.roadline.Data.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import konkukSW.MP2019.roadline.Data.Dataclass.PickDate
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.item_date_button.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DayListAdapter(
        private val onItemClickListener: OnItemClickListener): ListAdapter<PickDate, DayListAdapter.DayViewHolder>(
    DayDiffUtil()
) {
    interface OnItemClickListener {
        fun onItemClick(dayNum: Int?)
    }

    inner class DayViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: PickDate){
            itemView.apply{
                if(android.os.Build.VERSION.SDK_INT >= 26) {
                    val date = LocalDate.ofEpochDay(item.date)
                    tvDateIcon.text = date.format(DateTimeFormatter.ofPattern("d"))
                    tvDate.text = date.format(DateTimeFormatter.ofPattern("M"))
                } else {
                    val date = org.threeten.bp.LocalDate.ofEpochDay(item.date)
                    tvDateIcon.text = date.format(org.threeten.bp.format.DateTimeFormatter.ofPattern("d"))
                    tvDate.text = date.format(org.threeten.bp.format.DateTimeFormatter.ofPattern("M"))
                }
            }
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(item.day-1)
                it.tvDateIcon.isSelected = true
            }
            itemView.tvDateIcon.isSelected = item.isSelected
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_date_button,parent,false)
        return DayViewHolder(v)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DayDiffUtil : DiffUtil.ItemCallback<PickDate>() {
    override fun areItemsTheSame(oldItem: PickDate, newItem: PickDate): Boolean {
        return oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: PickDate, newItem: PickDate): Boolean {
        return oldItem == newItem
    }
}
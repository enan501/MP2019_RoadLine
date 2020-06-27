package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.item_addlist_currency.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CurrencyAdapter(var items: ArrayList<T_Currency>, val context: Context): RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnLongClick(position: Int):Boolean
    }

    var itemClickListener : OnItemClickListener? = null

    inner class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        init{
            itemView.setOnLongClickListener{
                itemClickListener!!.OnLongClick(adapterPosition)
            }
        }
        fun bind(item: T_Currency){
            itemView.curText.text = item.code
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_addlist_currency,p0,false)
        return ViewHolder(v)

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

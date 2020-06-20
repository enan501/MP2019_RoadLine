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
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainListAdapter(items: OrderedRealmCollection<T_List>, val context: Context): RealmRecyclerViewAdapter<T_List, MainListAdapter.ViewHolder>(items, true) {
    var realm: Realm

    init {
        Realm.init(context)
        realm = Realm.getDefaultInstance()
    }

    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, data: T_List, position: Int)
        fun OnEditClick(holder:ViewHolder, data: T_List, position: Int)
        fun OnDeleteClick(holder:ViewHolder, data: T_List, position: Int)
    }

    var itemClickListener : OnItemClickListener? = null

    inner class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        var title: TextView
        var date: TextView
        var image: ImageView
        var edit: ImageButton
        var delete: ImageButton
        init{
            title = itemView.findViewById(R.id.MLItem_title)
            date = itemView.findViewById(R.id.MLItem_date)
            image = itemView.findViewById(R.id.MLItem_image)
            edit = itemView.findViewById(R.id.ML_edit)
            delete = itemView.findViewById(R.id.ML_delete)
            itemView.setOnClickListener{
                itemClickListener?.OnItemClick(this, getItem(adapterPosition)!!, adapterPosition)
            }
            edit.setOnClickListener {
                itemClickListener?.OnEditClick(this,getItem(adapterPosition)!!,adapterPosition)
            }
            delete.setOnClickListener {
                itemClickListener?.OnDeleteClick(this,getItem(adapterPosition)!!,adapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_main_list,p0,false)
        return ViewHolder(v)

    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val item = getItem(p1)!!
        p0.title.text = item.title
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            val dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val dateSt = LocalDate.ofEpochDay(item.dateStart)
            val dateEn = LocalDate.ofEpochDay(item.dateEnd)
            p0.date.text = dateFormat.format(dateSt) + " ~ " + dateFormat.format(dateEn)
        }
        else{
            val dateForamt = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")
            val dateSt = org.threeten.bp.LocalDate.ofEpochDay(item.dateStart)
            val dateEn = org.threeten.bp.LocalDate.ofEpochDay(item.dateEnd)
            p0.date.text = dateSt.format(dateForamt) + " ~ " + dateEn.format(dateForamt)
        }
        if(getItem(p1)!!.img.isEmpty()){
            p0.image.setImageResource(R.drawable.ml_default_image)
        }
        else{
            p0.image.setImageURI(Uri.parse(item.img))
        }
    }
}

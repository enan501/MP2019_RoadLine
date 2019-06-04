package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.Dataclass.MainList
import konkukSW.MP2019.roadline.R

class MainListAdapter(var items:ArrayList<MainList>, val context: Context): RecyclerView.Adapter<MainListAdapter.ViewHolder>() {
    
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_main_list,p0,false)
        return ViewHolder(v)
    }
    fun moveItem(pos1:Int, pos2:Int){
        var temp = items[pos1]
        items[pos1] = items[pos2]
        items[pos2] = temp
        changePos()
        notifyItemMoved(pos1,pos2)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun changePos(){
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        for(i in 0..items.size-1){
            val id = items[i].id
            val result: T_List = realm.where<T_List>(T_List::class.java).equalTo("id", id).findFirst()!!
            result.pos = i
        }
        realm.commitTransaction()
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.title.text =items.get(p1).title
        p0.date.text = items.get(p1).date
        if(items.get(p1).image.isEmpty()){
            //TODO("대표이미지 설정")
            p0.image.setImageResource(R.drawable.ml_default_image)
        }
        else{
            //p0.image.setImageResource(items.get(p1).image)
        }
    }
    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, data: MainList, position: Int)
        fun OnEditClick(holder:ViewHolder, data: MainList, position: Int)
        fun OnDeleteClick(holder:ViewHolder, data: MainList, position: Int)
    }
    interface OnItemLongClickListener {
        fun OnItemLongClick(holder: ViewHolder, view: View, data: MainList, position: Int)
    }
    var itemClickListener : OnItemClickListener? = null
    var itemLongClickListener : OnItemLongClickListener? = null

    inner class ViewHolder(itemView: View)
        :RecyclerView.ViewHolder(itemView){
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
                val position = adapterPosition
                itemClickListener?.OnItemClick(this,items[position],position)
            }
            itemView.setOnLongClickListener{
                val position = adapterPosition
                itemLongClickListener?.OnItemLongClick(this,it, items[position],position)
                true
            }
            edit.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnEditClick(this,items[position],position)
            }
            delete.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnDeleteClick(this,items[position],position)
            }

        }

    }
}

package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.libraries.places.internal.i
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.AddSpotActivity
import konkukSW.MP2019.roadline.UI.date.Fragment1
import konkukSW.MP2019.roadline.UI.date.Fragment2
import kotlinx.android.synthetic.main.activity_show_date.*

class DateListAdapter(val items:ArrayList<Plan>, val listener: ItemDragListener, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private val TYPE_ITEM:Int = 0
    private val TYPE_FOOTER:Int = 1

    interface OnItemClickListener{
        fun OnItemClick(holder:ItemViewHolder, view:View, data: Plan, position: Int)
        fun OnItemClick(holder:FooterViewHolder)
    }

    var itemClickListener :OnItemClickListener? = null

    interface ItemDragListener {
        fun onStartDrag(holder: RecyclerView.ViewHolder)
    }

    fun moveItem(pos1:Int, pos2:Int){ //객체 두개 바꾸기 함수
        if(pos2 <= items.size - 1){
            val item1 = items.get(pos1)
            items.removeAt(pos1)
            items.add(pos2, item1)
            notifyItemMoved(pos1, pos2)
            changePos()

            for(i in 0..items.size-1) {
                if (items.size == 1 && i == 0)
                    items.get(i).viewType = -2
                else if (items.size > 1 && i == items.size - 1)
                    items.get(i).viewType = -3
                else if (items.size > 1 && i == 0)
                    items.get(i).viewType = -4
                else if (items.size > 1)
                    items.get(i).viewType = -1
            }
            notifyDataSetChanged()

        }
    }

    fun removeItem(pos:Int){ // 객체 지우기 함수
            Realm.init(context)
            val realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            val q = realm.where(T_Plan::class.java).findAll()
            val tuple = realm.where(T_Plan::class.java)
                .equalTo("id", items.get(pos).id)
                .equalTo("pos", pos)
                .findFirst()
            tuple!!.deleteFromRealm()
            realm.commitTransaction()
            items.removeAt(pos)
            notifyItemRemoved(pos)
            changePos()

        for(i in 0..items.size-1) {
            if (items.size == 1 && i == 0)
                items.get(i).viewType = -2
            else if (items.size > 1 && i == items.size - 1)
                items.get(i).viewType = -3
            else if (items.size > 1 && i == 0)
                items.get(i).viewType = -4
            else if (items.size > 1)
                items.get(i).viewType = -1
        }
        notifyDataSetChanged()
    }

    fun changePos(){
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        for(i in 0..items.size-1){
            val id = items[i].id
            val result:T_Plan = realm.where<T_Plan>(T_Plan::class.java).equalTo("id", id).findFirst()!!
            result.pos = i
        }
        realm.commitTransaction()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        var v:View
        if(p1 == TYPE_ITEM){
            v = LayoutInflater.from(p0.context).inflate(R.layout.row_spot, p0, false)
            return  ItemViewHolder(v, listener)
        }else{
            v = LayoutInflater.from(p0.context).inflate(R.layout.row_spot_footer, p0, false)
            return  FooterViewHolder(v)
        }
    }

    override fun getItemCount(): Int {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return items.size + 1
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) { //viewHolder의 내용 초기화
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if(p0 is ItemViewHolder) {
            p0.spotName.text = items.get(p1).name

            if(items.get(p1).viewType == -1)
                p0.listimg.setImageResource(R.drawable.ver_mid)
            else if(items.get(p1).viewType == -2)
                p0.listimg.setImageResource(R.drawable.ver_one)
            else if(items.get(p1).viewType == -3)
                p0.listimg.setImageResource(R.drawable.ver_down)
            else if(items.get(p1).viewType == -4)
                p0.listimg.setImageResource(R.drawable.ver_top)

        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position == items.size ){
            return TYPE_FOOTER
        }
        else{
            return TYPE_ITEM
        }
    }

    inner class ItemViewHolder(itemView: View, listener: ItemDragListener) : RecyclerView.ViewHolder(itemView) { //데이터 저장 구조
        var spotName: TextView
        var dragBtn: ImageView
        var listimg: ImageView

        init {
            spotName = itemView.findViewById(R.id.rs_spotName)
            dragBtn = itemView.findViewById(R.id.rs_dragBtn)
            listimg = itemView.findViewById(R.id.list_img)
            dragBtn.setOnTouchListener { v, event ->
                if(event.action == MotionEvent.ACTION_DOWN){
                    listener.onStartDrag(this)
                }
                false
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }

        }

    }
    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //데이터 저장 구조
        var addBtn: ImageView

        init {
            addBtn = itemView.findViewById(R.id.rsf_addBtn)
            addBtn.setOnClickListener {
                itemClickListener?.OnItemClick(this)
            }
        }

    }
}
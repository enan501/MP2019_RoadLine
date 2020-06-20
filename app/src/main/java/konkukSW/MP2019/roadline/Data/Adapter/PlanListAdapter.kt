package konkukSW.MP2019.roadline.Data.Adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.Fragment1
import kotlinx.android.synthetic.main.fragment_fragment1.view.*
import java.lang.Exception

class PlanListAdapter (realmResult: OrderedRealmCollection<T_Plan>, val context: Context) : RealmRecyclerViewAdapter<T_Plan, RecyclerView.ViewHolder>(realmResult, true) {

    private val TYPE_ITEM = 0
    private val TYPE_FOOTER = 1

    interface OnItemClickListener{
        fun OnItemClick(holder: ItemViewHolder, view:View, data: T_Plan, position: Int)
        fun OnItemClick(holder: FooterViewHolder)
    }

    interface OnItemDragListener{
        fun onStartDrag(holder: RecyclerView.ViewHolder)
    }

    interface OnItemLongClickListener{
        fun onItemLongClick()
    }

    interface OnItemChangeListener{
        fun onItemChange()
    }

    var itemClickListener :OnItemClickListener? = null
    var itemDragListener :OnItemDragListener? = null
    var itemLongClickListener:OnItemLongClickListener? = null
    var itemChangeListener:OnItemChangeListener? = null
    var realm: Realm

    init {
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        for(i in 0..itemCount-2){
            Log.d("mytag", "pos : " + getItem(i)?.pos.toString())
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var spotName: TextView
        var spotTime: TextView
        var dragBtn: ImageView
        var deleteBtn:ImageView
        init{
            spotName = itemView.findViewById(R.id.rs_spotName)
            deleteBtn = itemView.findViewById(R.id.rs_deleteBtn)
            spotTime = itemView.findViewById(R.id.rs_spotTime)
            dragBtn = itemView.findViewById(R.id.rs_dragBtn)

            dragBtn.setOnTouchListener { _, event ->
                if(event.action == MotionEvent.ACTION_DOWN){
                    itemDragListener?.onStartDrag(this)
                }
                false
            }
            deleteBtn.setOnClickListener {
                val builder = AlertDialog.Builder(context) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
                val deleteListDialog = //context 이용해서 레이아웃 인플레이터 생성
                        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.delete_list_dialog, null)
                val deleteListText = deleteListDialog.findViewById<TextView>(R.id.DL_textView)
                var deleteMessage = "이 항목을 삭제하시겠습니까?"
                deleteListText.text = deleteMessage

                builder.setView(deleteListDialog)
                        .setPositiveButton("삭제") { _, _->
                            removeItem(adapterPosition)
                        }
                        .setNegativeButton("취소") { _, _ ->
                        }
                        .show()
            }
            itemView.setOnClickListener {
                if(adapterPosition in 0 until itemCount - 1)
                    itemClickListener?.OnItemClick(this, it, getItem(adapterPosition)!!, adapterPosition)
            }
            itemView.setOnLongClickListener {_ ->
                itemLongClickListener?.onItemLongClick()
                true
            }
        }
    }

    fun moveItem(pos1:Int, pos2:Int){  //객체 두개 바꾸기 함수
        if (pos2 in 0 until itemCount - 1 && pos1 in 0 until itemCount - 1) {
            Log.d("mytag", "moveItem!!")
            realm.beginTransaction()
            var item1 = getItem(pos1)!!
            if(pos2 > pos1) {
                for (i in pos1 + 1..pos2) {
                    getItem(i)!!.pos--
                }
            }
            else{
                for (i in pos2 until pos1){
                    getItem(i)!!.pos++
                }
            }
            item1.pos = pos2
            realm.commitTransaction()
            notifyItemMoved(pos1,pos2)
        }

    }

    fun removeItem(pos:Int){ // 객체 지우기 함수
        realm.beginTransaction()
//        val tuple = realm.where(T_Plan::class.java)
//                .equalTo("id", items[pos].id)
//                .findFirst()

//        tuple!!.deleteFromRealm()
        Log.d("mytag", "pos " + pos.toString())
        getItem(pos)?.deleteFromRealm()
        for(i in pos + 1 .. itemCount - 2) {
            getItem(i)!!.pos--
        }
        realm.commitTransaction()
//        items.removeAt(pos)
//
//        notifyItemRangeRemoved(pos, items.size + 1)
//        changePos()
        itemChangeListener!!.onItemChange() //iconAdapter 다시 달기
    }

//    fun changePos(){
//        realm.beginTransaction()
//        for(i in 0..items.size-1) {
//            val id = items[i].id
//            val result = realm.where(T_Plan::class.java)
//                    .equalTo("id", id)
//                    .findFirst()
//            items[i].pos = i
//            result!!.pos = i
//        }
//        realm.commitTransaction()
//    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //데이터 저장 구조
        var addBtn: ImageView

        init {
            addBtn = itemView.findViewById(R.id.rsf_addBtn)
            addBtn.setOnClickListener {
                itemClickListener?.OnItemClick(this)
            }
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v:View
        if(viewType == TYPE_ITEM){
            v = LayoutInflater.from(p0.context).inflate(R.layout.row_spot, p0, false)
            return  ItemViewHolder(v)
        }else{
            v = LayoutInflater.from(p0.context).inflate(R.layout.row_spot_footer, p0, false)
            return  FooterViewHolder(v)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position == super.getItemCount()){
            return TYPE_FOOTER
        }
        else{
            return TYPE_ITEM
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if(p0 is ItemViewHolder) {
            p0.spotName.text = getItem(p1)?.name
            if(getItem(p1)?.hour != null)
                p0.spotTime.text = getItem(p1)?.hour.toString() + ":" + getItem(p1)?.minute.toString()
            if(Fragment1.editMode){
                p0.deleteBtn.visibility = View.VISIBLE
                p0.dragBtn.visibility = View.INVISIBLE
            }
            else{
                p0.deleteBtn.visibility = View.INVISIBLE
                p0.dragBtn.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }
}
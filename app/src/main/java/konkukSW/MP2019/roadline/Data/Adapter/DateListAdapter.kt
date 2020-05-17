package konkukSW.MP2019.roadline.Data.Adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.Fragment1
import kotlinx.android.synthetic.main.row_spot.view.*

class DateListAdapter(val items:ArrayList<Plan>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ITEM:Int = 0
    private val TYPE_FOOTER:Int = 1

    interface OnItemClickListener{
        fun OnItemClick(holder:ItemViewHolder, view:View, data: Plan, position: Int)
        fun OnItemClick(holder:FooterViewHolder)
    }

    interface OnItemDragListener{
        fun onStartDrag(holder: RecyclerView.ViewHolder)
    }

    interface OnItemLongClickListener{
        fun onItemLongClick()
    }

    var itemClickListener :OnItemClickListener? = null
    var itemDragListener :OnItemDragListener? = null
    var itemLongClickListener:OnItemLongClickListener? = null

    fun moveItem(pos1:Int, pos2:Int){  //객체 두개 바꾸기 함수
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
        for(i in 0..items.size-1)
        {
            val id = items[i].id
            val result = realm.where<T_Plan>(T_Plan::class.java)
                    .equalTo("id", id)
                    .findFirst()
            items[i].pos = i
            result!!.pos = i
        }
        realm.commitTransaction()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        var v:View
        if(p1 == TYPE_ITEM){
            v = LayoutInflater.from(p0.context).inflate(R.layout.row_spot, p0, false)
            return  ItemViewHolder(v)
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
            p0.spotTime.text = items.get(p1).time

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

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //데이터 저장 구조
        var spotName: TextView
        var spotTime: TextView
        var dragBtn: ImageView
        var listimg: ImageView
        var deleteBtn:ImageView

        init {
            spotName = itemView.findViewById(R.id.rs_spotName)
            deleteBtn = itemView.findViewById(R.id.rs_deleteBtn)
            spotTime = itemView.findViewById(R.id.rs_spotTime)
            dragBtn = itemView.findViewById(R.id.rs_dragBtn)
            listimg = itemView.findViewById(R.id.list_img)
            if(Fragment1.editMode == true){
                dragBtn.visibility = View.VISIBLE
                deleteBtn.visibility = View.VISIBLE
            }
            dragBtn.setOnTouchListener { v, event ->
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
                        .setPositiveButton("삭제") { dialogInterface, _ ->
                            removeItem(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                        }
                        .setNegativeButton("취소") { dialogInterface, i ->
                        }
                        .show()
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }
            itemView.setOnClickListener {
                Log.d("mytest", adapterPosition.toString())
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }
            itemView.setOnLongClickListener {v ->
                itemLongClickListener?.onItemLongClick()
                true
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
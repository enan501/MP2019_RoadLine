package konkukSW.MP2019.roadline.UI

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.MainListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.Dataclass.MainList
import konkukSW.MP2019.roadline.UI.date.PickDateActivity
import kotlinx.android.synthetic.main.activity_main_list.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*



class MainListActivity : AppCompatActivity() {

    var MLArray:ArrayList<MainList> = arrayListOf(
        )
    lateinit var MLAdapter:MainListAdapter
    lateinit var realm: Realm



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_main_list)
        init()
    }
    fun init() {
        initData()
        initLayout()
        initSwipe()
//        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
//        realm.deleteAll()
//        realm.commitTransaction()
    }

    fun initLayout(){
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        ML_rView.layoutManager = layoutManager
        MLAdapter = MainListAdapter(MLArray,this)
        ML_rView.adapter = MLAdapter
        addListener()
    }
    fun initData(){
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        val results = realm.where<T_List>(T_List::class.java).findAll().sort("pos")
        for(T_List in results){
            MLArray.add(MainList(T_List.id,T_List.title,T_List.date,""))
        }
    }
    fun initSwipe(){
        val simpleItemTouchCallback = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT){
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                //
            }

            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                MLAdapter.moveItem(p1.adapterPosition,p2.adapterPosition)
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }

        } // 객체 생성
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(ML_rView)
    }

    fun addListener() {
        MLAdapter.itemClickListener = object : MainListAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MainListAdapter.ViewHolder, data: MainList, position: Int) {
                val MLIntent = Intent(applicationContext,PickDateActivity::class.java)
                MLIntent.putExtra("ListID",data.id)
                startActivity(MLIntent)
            }
            override fun OnEditClick(holder: MainListAdapter.ViewHolder, data: MainList, position: Int) {
                val builder = AlertDialog.Builder(this@MainListActivity) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
                val addListDialog = layoutInflater.inflate(konkukSW.MP2019.roadline.R.layout.add_list_dialog, null)
                val addListTitle = addListDialog.findViewById<EditText>(konkukSW.MP2019.roadline.R.id.AL_title)
                val addListDate = addListDialog.findViewById<DatePicker>(konkukSW.MP2019.roadline.R.id.AL_date)
                val item = realm.where(T_List::class.java)
                    .equalTo("id",data.id)
                    .findFirst()
                addListTitle.setText(item!!.title)
                val date = LocalDate.parse(item.date, DateTimeFormatter.ofPattern("yyyy. M. d"))
                addListDate.updateDate(date.year,date.monthValue,date.dayOfMonth)

                builder.setView(addListDialog)
                    .setPositiveButton("수정") { dialogInterface, _ ->
                        realm.beginTransaction()
                        item.title = addListTitle.text.toString()
                        item.date = addListDate.year.toString() +". "+ (addListDate.month+1).toString() +". "+ addListDate.dayOfMonth.toString()
                        val Days = realm.where(T_Day::class.java).equalTo("listID",item.id).findAll()
                        var i = 0L
                        for (day in Days){
                            day.date = LocalDate.parse(item.date, DateTimeFormatter.ofPattern("yyyy. M. d")).plusDays(i++)
                                .format( DateTimeFormatter.ofPattern("yyyy. M. d"))
                        }
                        realm.commitTransaction()
                        MLArray[position].title = item.title
                        MLArray[position].date = item.date
                        MLAdapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("취소") { dialogInterface, i ->
                    }
                    .show()
            }

            override fun OnDeleteClick(holder: MainListAdapter.ViewHolder, data: MainList, position: Int) {

            }

        }
        MLAdapter.itemLongClickListener = object : MainListAdapter.OnItemLongClickListener {
            override fun OnItemLongClick(
                holder: MainListAdapter.ViewHolder,view: View,data: MainList,position: Int) {
                Log.d("longclicked", "LongClicked")
            }
        }


//        var gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
//            override fun onSingleTapUp(e: MotionEvent): Boolean {
//                return true
//            }
//        })
//        var onItemTouchListener = object : RecyclerView.OnItemTouchListener{
//            override fun onTouchEvent(rView: RecyclerView, e: MotionEvent) {
//
//            }
//            override fun onInterceptTouchEvent(rView: RecyclerView, e: MotionEvent): Boolean {
//                var childView = rView.findChildViewUnder(e.getX(),e.getY())
//                if(childView != null && gestureDetector.onTouchEvent(e)){
//                    var pos = rView.getChildAdapterPosition(childView)
//                    //롱클릭 했을때
//                    Log.d("longclicked",pos.toString())
//                    return true
//                }
//                return false
//            }
//            override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {
//            }
//        }
//        ML_rView.addOnItemTouchListener(onItemTouchListener)

        ML_addListBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
            val addListDialog = layoutInflater.inflate(konkukSW.MP2019.roadline.R.layout.add_list_dialog, null)
            val addListTitle = addListDialog.findViewById<EditText>(konkukSW.MP2019.roadline.R.id.AL_title)
            val addListDate = addListDialog.findViewById<DatePicker>(konkukSW.MP2019.roadline.R.id.AL_date)
            builder.setView(addListDialog)
                .setPositiveButton("추가") { dialogInterface, _ ->
                    realm.beginTransaction()
                    val newList = realm.createObject(T_List::class.java, UUID.randomUUID().toString())
                    newList.title = addListTitle.text.toString()
                    newList.date = addListDate.year.toString() +". "+ (addListDate.month+1).toString() +". "+ addListDate.dayOfMonth.toString()
                    val newDay  = realm.createObject(T_Day::class.java)
                    newDay.listID = newList.id
                    newDay.date = newList.date
                    newDay.num = 1
                    realm.commitTransaction()
                    MLArray.add(MainList(newList.id,newList.title,newList.date,""))
                    MLAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
    }
}

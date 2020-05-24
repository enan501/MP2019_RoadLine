package konkukSW.MP2019.roadline.UI

import android.app.Activity
import android.app.DatePickerDialog
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
import android.widget.TextView
import android.widget.Toast
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.MainListAdapter
import konkukSW.MP2019.roadline.Data.DB.*
import konkukSW.MP2019.roadline.Data.Dataclass.MainList
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.PickDateActivity
import kotlinx.android.synthetic.main.activity_main_list.*
import java.text.DateFormat
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*



class MainListActivity : AppCompatActivity() {


    var MLArray:ArrayList<MainList> = arrayListOf()
    lateinit var MLAdapter:MainListAdapter
    lateinit var realm: Realm
    private val REQUEST_CODE = 123

//    override fun onResume() {
//        super.onResume()
//        updateImg()
//        MLAdapter.notifyDataSetChanged()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_main_list)
        init()
    }
    fun init() {
        initData()
        initLayout()
        initSwipe()
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
            MLArray.add(MainList(T_List.id, T_List.title, T_List.dateStart, T_List.dateEnd, T_List.img))
        }
        if(MLArray.size == 0) {
            ML_rView.visibility = View.GONE
            startText.visibility = View.VISIBLE
        } else {
            ML_rView.visibility = View.VISIBLE
            startText.visibility = View.GONE
        }
    }
    fun updateImg(pos:Int){
        realm = Realm.getDefaultInstance()
        val result = realm.where<T_List>(T_List::class.java).equalTo("id", MLArray[pos].id).findFirst()
//        for(i in 0..MLArray.size-1){
//            if(MLArray[i].image != results[i]!!.img) {
//                MLArray[i].image = results[i]!!.img
//            }
//        }

        if(result!!.img != MLArray[pos].image){
            MLArray[pos].image = result!!.img
        }
        MLArray[pos].dateEnd = result!!.dateEnd
    }

    fun initSwipe(){
        val simpleItemTouchCallback = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT){
            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE){
            val pos = resultCode
            Log.d("mytag", pos.toString())
            updateImg(pos)
            MLAdapter.notifyItemChanged(pos)
        }
    }

    fun addListener() {
        MLAdapter.itemClickListener = object : MainListAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MainListAdapter.ViewHolder, data: MainList, position: Int) {
                val MLIntent = Intent(applicationContext, PickDateActivity::class.java)
                MLIntent.putExtra("ListID",data.id)
                MLIntent.putExtra("listPos", position)
                startActivityForResult(MLIntent, REQUEST_CODE)
            }
            override fun OnEditClick(holder: MainListAdapter.ViewHolder, data: MainList, position: Int) {
                val builder = AlertDialog.Builder(this@MainListActivity) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
                val addListDialog = layoutInflater.inflate(konkukSW.MP2019.roadline.R.layout.add_list_dialog, null)
                val addListText = addListDialog.findViewById<TextView>(konkukSW.MP2019.roadline.R.id.AL_text)
                val addListTitle = addListDialog.findViewById<EditText>(konkukSW.MP2019.roadline.R.id.AL_title)
                addListText.text = "여행 수정"
                val item = realm.where(T_List::class.java)
                    .equalTo("id", data.id)
                    .findFirst()
                addListTitle.setText(item!!.title)

                val editStart = addListDialog.findViewById<TextView>(R.id.editStart)
                val editEnd = addListDialog.findViewById<TextView>(R.id.editEnd)
                var dateStart:LocalDate = LocalDate.parse(item!!.dateStart, DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                var dateEnd:LocalDate = LocalDate.parse(item!!.dateEnd, DateTimeFormatter.ofPattern("yyyy.MM.dd"))

                editStart.setText(item!!.dateStart)
                editEnd.setText(item!!.dateEnd)

                val startListener = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                    dateStart = LocalDate.of(year, month + 1, dayOfMonth)
                    editStart.setText(dateStart.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                }

                val endListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    dateEnd = LocalDate.of(year, month + 1, dayOfMonth)
                    editEnd.setText(dateEnd.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                }

                editStart.setOnClickListener {
                    var dialog = DatePickerDialog(this@MainListActivity, startListener, dateStart.year, dateStart.monthValue - 1, dateStart.dayOfMonth)
                    dialog.show()
                }

                editEnd.setOnClickListener {
                    var dialog = DatePickerDialog(this@MainListActivity, endListener, dateEnd.year, dateEnd.monthValue - 1, dateEnd.dayOfMonth)
                    val c = Calendar.getInstance()
                    c.set(dateStart!!.year, dateStart!!.monthValue - 1, dateStart!!.dayOfMonth)
                    dialog.datePicker.minDate = c.timeInMillis
                    dialog.show()
                }

                builder.setView(addListDialog)
                    .setPositiveButton("수정") { dialogInterface, _ ->
                    }
                    .setNegativeButton("취소") { dialogInterface, i ->
                    }

                val cbuilder = builder.create()
                cbuilder.setCanceledOnTouchOutside(false)
                cbuilder.show()

                cbuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if(addListTitle.text.trim().toString() == ""){
                        Toast.makeText(applicationContext, "여행 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        if (dateStart != null && dateEnd != null) {
                            if (dateStart!!.isBefore(dateEnd) || dateStart!!.isEqual(dateEnd)) {
                                realm.beginTransaction()
                                item.title = addListTitle.text.toString()
                                item.dateStart = dateStart.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                                item.dateEnd = dateEnd.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                                realm.where(T_Day::class.java).equalTo("listID", item.id).findAll().deleteAllFromRealm()
                                val pnum = (dateStart.until(dateEnd, ChronoUnit.DAYS) + 1).toInt()
                                for (i in 1..pnum) {
                                    val newDay = realm.createObject(T_Day::class.java)
                                    newDay.listID = item.id
                                    newDay.date = dateStart.plusDays((i - 1).toLong())
                                            .format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                                    newDay.num = i
                                }
                                realm.commitTransaction()
                                MLArray[position].title = item.title
                                MLArray[position].dateStart = item.dateStart
                                MLArray[position].dateEnd = item.dateEnd
                                MLAdapter.notifyItemChanged(position)
                                cbuilder.dismiss()
                            } else {
                                Toast.makeText(applicationContext, "종료일이 시작일보다 이전일수 없습니다", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "시작일과 종료일 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            override fun OnDeleteClick(holder: MainListAdapter.ViewHolder, data: MainList, position: Int) {
                val builder = AlertDialog.Builder(this@MainListActivity) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
                val deleteListDialog =
                    layoutInflater.inflate(R.layout.delete_list_dialog, null)
                val deleteListText = deleteListDialog.findViewById<TextView>(R.id.DL_textView)
                val item = realm.where(T_List::class.java)
                    .equalTo("id", data.id)
                    .findFirst()
                var deleteMessage = "'"+item!!.title+deleteListText.text
                deleteListText.text = deleteMessage

                builder.setView(deleteListDialog)
                    .setPositiveButton("삭제") { dialogInterface, _ ->
                        realm.beginTransaction()

                        realm.where(T_Day::class.java).equalTo("listID", item.id).findAll().deleteAllFromRealm()
                        realm.where(T_Money::class.java).equalTo("listID", item.id).findAll().deleteAllFromRealm()
                        realm.where(T_Photo::class.java).equalTo("listID", item.id).findAll().deleteAllFromRealm()
                        realm.where(T_Plan::class.java).equalTo("listID", item.id).findAll().deleteAllFromRealm()
                        item.deleteFromRealm()
                        realm.commitTransaction()
                        MLAdapter.removeItem(position)
                        if(MLArray.size == 0) {
                            ML_rView.visibility = View.GONE
                            startText.visibility = View.VISIBLE
                        } else {
                            ML_rView.visibility = View.VISIBLE
                            startText.visibility = View.GONE
                        }
                        MLAdapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("취소") { dialogInterface, i ->

                    }
                    .show()
            }

        }
        MLAdapter.itemLongClickListener = object : MainListAdapter.OnItemLongClickListener {
            override fun OnItemLongClick(
                holder: MainListAdapter.ViewHolder,view: View,data: MainList,position: Int) {
                Log.d("longclicked", "LongClicked")
            }
        }

        ML_addListBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
            val addListDialog = layoutInflater.inflate(R.layout.add_list_dialog, null)
            val addListTitle = addListDialog.findViewById<EditText>(R.id.AL_title)
            val editStart = addListDialog.findViewById<TextView>(R.id.editStart)
            val editEnd = addListDialog.findViewById<TextView>(R.id.editEnd)
            var dateStart:LocalDate? = null
            var dateEnd:LocalDate? = null

            val startListener = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                dateStart = LocalDate.of(year, month + 1, dayOfMonth)
                editStart.setText(dateStart!!.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            }

            val endListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                dateEnd = LocalDate.of(year, month + 1, dayOfMonth)
                editEnd.setText(dateEnd!!.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            }

            editStart.setOnClickListener {
                var dialog:DatePickerDialog
                if(dateStart == null)
                    dialog = DatePickerDialog(this@MainListActivity, startListener, LocalDate.now().year, LocalDate.now().monthValue - 1, LocalDate.now().dayOfMonth)
                else {
                    dialog = DatePickerDialog(this@MainListActivity, startListener, dateStart!!.year, dateStart!!.monthValue - 1, dateStart!!.dayOfMonth)
                }
                dialog.show()
            }

            editEnd.setOnClickListener {
                var dialog:DatePickerDialog
                if(dateEnd == null){
                    if(dateStart != null){
                        dialog = DatePickerDialog(this@MainListActivity, endListener, dateStart!!.year, dateStart!!.monthValue - 1, dateStart!!.dayOfMonth)
                    }
                    else{
                        dialog = DatePickerDialog(this@MainListActivity, endListener, LocalDate.now().year, LocalDate.now().monthValue - 1, LocalDate.now().dayOfMonth)
                    }
                }
                else {
                    dialog = DatePickerDialog(this@MainListActivity, endListener, dateEnd!!.year, dateEnd!!.monthValue - 1, dateEnd!!.dayOfMonth)
                }
                val c = Calendar.getInstance()
                c.set(dateStart!!.year, dateStart!!.monthValue - 1, dateStart!!.dayOfMonth)
                dialog.datePicker.minDate = c.timeInMillis
                dialog.show()
            }

            builder.setView(addListDialog)
                .setPositiveButton("추가") { dialogInterface, _ ->
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
            
            val cbuilder = builder.create()
            cbuilder.setCanceledOnTouchOutside(false)
            cbuilder.show()

            cbuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if(addListTitle.text.trim().toString() == ""){
                    Toast.makeText(applicationContext, "여행 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                else{
                    if(dateStart != null && dateEnd != null) {
                        if(dateStart!!.isBefore(dateEnd) || dateStart!!.isEqual(dateEnd)){
                            realm.beginTransaction()

                            val newList = realm.createObject(T_List::class.java, UUID.randomUUID().toString())
                            newList.title = addListTitle.text.toString()
                            newList.dateStart = dateStart!!.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                            newList.dateEnd = dateEnd!!.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))

                            val pnum = (dateStart!!.until(dateEnd, ChronoUnit.DAYS) + 1).toInt()
                            for(i in 1..pnum){
                                val newDay  = realm.createObject(T_Day::class.java)
                                newDay.listID = newList.id
                                newDay.date = dateStart!!.plusDays((i - 1).toLong()).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                                newDay.num = i
                            }
                            realm.commitTransaction()

                            MLArray.add(MainList(newList.id, newList.title,newList.dateStart, newList.dateEnd,""))
                            if(MLArray.size == 0) {
                                ML_rView.visibility = View.GONE
                                startText.visibility = View.VISIBLE
                            } else {
                                ML_rView.visibility = View.VISIBLE
                                startText.visibility = View.GONE
                            }
                            MLAdapter.notifyDataSetChanged()
                            cbuilder.dismiss()
                        }
                        else{
                            Toast.makeText(applicationContext, "종료일이 시작일보다 이전일수 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(applicationContext, "시작일과 종료일 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}

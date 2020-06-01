package konkukSW.MP2019.roadline.UI

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.MainListAdapter
import konkukSW.MP2019.roadline.Data.DB.*
import konkukSW.MP2019.roadline.Data.Dataclass.MainList
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.PickDateActivity
import kotlinx.android.synthetic.main.activity_main_list.*
import kotlinx.android.synthetic.main.add_list_dialog.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*



class MainListActivity : AppCompatActivity() {


    var MLArray:ArrayList<MainList> = arrayListOf()
    lateinit var MLAdapter:MainListAdapter
    lateinit var currencyAdapter: ArrayAdapter<String>
    var realm = Realm.getDefaultInstance()
    private val REQUEST_CODE = 123
    private val CURRENCY_MAX_SIZE = 5
    //var korIndex = 0

//    override fun onResume() {
//        super.onResume()
//        updateImg()
//        MLAdapter.notifyDataSetChanged()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_main_list)
        Realm.init(this)
        init()
    }

    fun init() {
        initData()
        initLayout()
        initAdapter()
        initSwipe()
    }

    fun initAdapter(){
        currencyAdapter = object :ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var convertView = convertView
                if(convertView == null){
                    convertView = LayoutInflater.from(this@MainListActivity).inflate(android.R.layout.simple_spinner_item, null)
                }
                (convertView as TextView).text = "추가하기"
                convertView.setTextColor(ContextCompat.getColor(this@MainListActivity, R.color.colorPrimary))
                convertView.gravity = View.TEXT_ALIGNMENT_CENTER
                convertView.textSize = 15f
                return convertView
            }

//            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//                var itemView:View
//                if(convertView == null){
//                    itemView = LayoutInflater.from(this@MainListActivity).inflate(android.R.layout.simple_spinner_dropdown_item, null)
//                }
//                else{
//                    itemView = convertView
//                }
//                (itemView as TextView).text = getItem(position)
//                if(position == 0){
//                    (itemView as TextView).setTextColor(ContextCompat.getColor(this@MainListActivity, android.R.color.holo_red_dark))
//                }
//                return itemView
//            }

            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }

        var curTable = realm.where(T_Currency::class.java).findAll()
        for (T_currency in curTable) {
            currencyAdapter.add(T_currency.code + " - " + T_currency.name)
        }
        currencyAdapter.add("추가하기")
    }

    fun initLayout(){
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
        )
        ML_rView.layoutManager = layoutManager
        MLAdapter = MainListAdapter(MLArray,this)
        ML_rView.adapter = MLAdapter
        addListener()
    }
    fun initData(){
        val results = realm.where<T_List>(T_List::class.java).findAll().sort("pos")
        for(T_List in results){
            MLArray.add(MainList(T_List.id, T_List.title, T_List.dateStart, T_List.dateEnd, T_List.img, T_List.currencys))
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
            override fun onSwiped(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
            }

            override fun onMove(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder, p2: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
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
                val item = realm.where(T_List::class.java).equalTo("id", data.id).findFirst()!!
                addListTitle.setText(item.title)

                val editStart = addListDialog.findViewById<TextView>(R.id.editStart)
                val editEnd = addListDialog.findViewById<TextView>(R.id.editEnd)

                val curTextArray = arrayListOf<TextView>()
                curTextArray.add(addListDialog.findViewById(R.id.curText0))
                curTextArray.add(addListDialog.findViewById(R.id.curText1))
                curTextArray.add(addListDialog.findViewById(R.id.curText2))
                curTextArray.add(addListDialog.findViewById(R.id.curText3))
                curTextArray.add(addListDialog.findViewById(R.id.curText4))
                val curArray = arrayListOf<T_Currency>()
                val currencySpinner = addListDialog.findViewById<Spinner>(R.id.currencySpinner)
                currencySpinner.adapter = currencyAdapter
                currencySpinner.setSelection(currencyAdapter.count)
                for(i in 0 until item.currencys.size){
                    curArray.add(item.currencys[i]!!)
                    curTextArray[i].text = item.currencys[i]!!.code
                    curTextArray[i].visibility = View.VISIBLE
                }
                currencySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        currencySpinner.setSelection(currencyAdapter.count)
                        if(position < currencySpinner.count){
                            val result = parent!!.getItemAtPosition(position).toString()
                            val code = result.split(" - ")[0].trim()

                            val curTuple = realm.where(T_Currency::class.java).equalTo("code", code).findFirst()
                            var exist = false
                            for(i in curArray){
                                if(curTuple!!.code == i.code){
                                    exist = true
                                }
                            }
                            if(curArray.size < CURRENCY_MAX_SIZE){
                                if(exist){
                                    Toast.makeText(applicationContext, "이미 선택한 화폐입니다", Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    curArray.add(curTuple!!)
                                    val index = curArray.size - 1
                                    curTextArray[index].visibility = View.VISIBLE
                                    curTextArray[index].text = curTuple!!.code
                                }
                            }
                            else{
                                Toast.makeText(applicationContext, "화폐는 5개까지 설정할 수 있습니다 ", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }


                curTextArray[0].setOnLongClickListener{
                    Toast.makeText(applicationContext, "한화는 기본 값으로 삭제할수 없습니다", Toast.LENGTH_SHORT).show()
                    false
                }

                for(i in 1 until CURRENCY_MAX_SIZE){
                    curTextArray[i].setOnLongClickListener {
                        val ynBuilder = AlertDialog.Builder(this@MainListActivity)
                        ynBuilder.setMessage(curTextArray[i].text.toString() + " 화폐를 삭제하시겠습니까?")
                                .setPositiveButton("삭제") { dialogInterface, _ ->
                                    Log.d("mytag", "cur longclick")
                                    val index = curArray.size - 1
                                    curArray.removeAt(i)
                                    curTextArray[index].visibility = View.INVISIBLE
                                    curTextArray[index].text = ""
                                    for(j in 1 until curArray.size){
                                        curTextArray[j].text = curArray[j].code
                                    }
                                }
                                .setNegativeButton("취소") { dialogInterface, i ->
                                }
                        val ynDialog = ynBuilder.create()
                        ynDialog.show()

                        false
                    }
                }

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
                                item.currencys.clear()
                                for(i in curArray){
                                    item.currencys.add(i)
                                }
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
                                MLArray[position].currencys = item.currencys
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

            val curTextArray = arrayListOf<TextView>()
            curTextArray.add(addListDialog.findViewById(R.id.curText0))
            curTextArray.add(addListDialog.findViewById(R.id.curText1))
            curTextArray.add(addListDialog.findViewById(R.id.curText2))
            curTextArray.add(addListDialog.findViewById(R.id.curText3))
            curTextArray.add(addListDialog.findViewById(R.id.curText4))
            val curArray = arrayListOf<T_Currency>()
            val currencySpinner = addListDialog.findViewById<Spinner>(R.id.currencySpinner)
            currencySpinner.adapter = currencyAdapter
            val korCur = realm.where(T_Currency::class.java).equalTo("code", "KRW").findFirst()!!
            curArray.add(korCur)
//            curTextArray[0].visibility = View.VISIBLE
//            curTextArray[0].text = korCur.code
            currencySpinner.setSelection(currencyAdapter.count)
            currencySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    currencySpinner.setSelection(currencyAdapter.count)
                    if(position < currencySpinner.count){
                        val result = parent?.getItemAtPosition(position).toString()
                        val code = result.split(" - ")[0].trim()

                        val curTuple = realm.where(T_Currency::class.java).equalTo("code", code).findFirst()
                        var exist = false
                        for(i in curArray){
                            if(curTuple!!.code == i.code){
                                exist = true
                            }
                        }
                        if(curArray.size < CURRENCY_MAX_SIZE){
                            if(exist){
                                Toast.makeText(applicationContext, "이미 선택한 화폐입니다", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                curArray.add(curTuple!!)
                                val index = curArray.size - 1
                                curTextArray[index].visibility = View.VISIBLE
                                curTextArray[index].text = curTuple!!.code
                            }
                        }
                        else{
                            Toast.makeText(applicationContext, "화폐는 5개까지 설정할 수 있습니다 ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }


            curTextArray[0].setOnLongClickListener{
                Toast.makeText(applicationContext, "한화는 기본 값으로 삭제할수 없습니다", Toast.LENGTH_SHORT).show()
                false
            }

            for(i in 1 until CURRENCY_MAX_SIZE){
                curTextArray[i].setOnLongClickListener {
                    val ynBuilder = AlertDialog.Builder(this)
                    ynBuilder.setMessage(curTextArray[i].text.toString() + " 화폐를 삭제하시겠습니까?")
                            .setPositiveButton("삭제") { dialogInterface, _ ->
                                Log.d("mytag", "cur longclick")
                                val index = curArray.size - 1
                                curArray.removeAt(i)
                                curTextArray[index].visibility = View.INVISIBLE
                                curTextArray[index].text = ""
                                for(j in 1 until curArray.size){
                                    curTextArray[j].text = curArray[j].code
                                }
                            }
                            .setNegativeButton("취소") { dialogInterface, i ->
                            }
                    val ynDialog = ynBuilder.create()
                    ynDialog.show()

                    false
                }
            }

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
                        val c = Calendar.getInstance()
                        c.set(dateStart!!.year, dateStart!!.monthValue - 1, dateStart!!.dayOfMonth)
                        dialog.datePicker.minDate = c.timeInMillis}
                    else{
                        dialog = DatePickerDialog(this@MainListActivity, endListener, LocalDate.now().year, LocalDate.now().monthValue - 1, LocalDate.now().dayOfMonth)
                    }
                }
                else {
                    dialog = DatePickerDialog(this@MainListActivity, endListener, dateEnd!!.year, dateEnd!!.monthValue - 1, dateEnd!!.dayOfMonth)
                }

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
                            for(i in curArray){
                                newList.currencys.add(i)
                            }

                            val pnum = (dateStart!!.until(dateEnd, ChronoUnit.DAYS) + 1).toInt()
                            for(i in 1..pnum){
                                val newDay  = realm.createObject(T_Day::class.java)
                                newDay.listID = newList.id
                                newDay.date = dateStart!!.plusDays((i - 1).toLong()).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                                newDay.num = i
                            }


                            realm.commitTransaction()


                            MLArray.add(MainList(newList.id, newList.title,newList.dateStart, newList.dateEnd,"", newList.currencys))
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

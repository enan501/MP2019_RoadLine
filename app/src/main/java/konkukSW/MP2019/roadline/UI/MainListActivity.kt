package konkukSW.MP2019.roadline.UI

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import konkukSW.MP2019.roadline.Data.Adapter.DateItemTouchHelperCallback
import konkukSW.MP2019.roadline.Data.Adapter.MainListAdapter
import konkukSW.MP2019.roadline.Data.DB.*
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.PickDateActivity
import kotlinx.android.synthetic.main.activity_main_list.*
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*



class MainListActivity : AppCompatActivity() {


    lateinit var MLAdapter:MainListAdapter
    lateinit var currencyAdapter: ArrayAdapter<String>
    lateinit var realm:Realm
    private val REQUEST_CODE = 123
    private val CURRENCY_MAX_SIZE = 5
    lateinit var listResults: RealmResults<T_List>
    lateinit var curResults: RealmResults<T_Currency>
    var nowYear = 0
    var nowMonth = 0
    var nowDay = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_main_list)
        Realm.init(this)
        init()
    }

    fun init() {
        initData()
        initLayout()
        initListener()
        initAdapter()
    }

    fun initData(){
        realm = Realm.getDefaultInstance()
        listResults = realm.where<T_List>(T_List::class.java).findAll().sort("dateStart")
        curResults = realm.where<T_Currency>(T_Currency::class.java).findAll()
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            nowYear = LocalDate.now().year
            nowMonth = LocalDate.now().monthValue - 1
            nowDay = LocalDate.now().dayOfMonth
        }
        else{
            nowYear =org.threeten.bp.LocalDate.now().year
            nowMonth = org.threeten.bp.LocalDate.now().monthValue - 1
            nowDay = org.threeten.bp.LocalDate.now().dayOfMonth
        }
    }

    fun initLayout(){
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this, RecyclerView.VERTICAL, false
        )
        ML_rView.layoutManager = layoutManager
        MLAdapter = MainListAdapter(listResults,this)
        ML_rView.adapter = MLAdapter
        ML_rView.setHasFixedSize(false)

        if(listResults.size == 0) {
            ML_rView.visibility = View.GONE
            startText.visibility = View.VISIBLE
        } else {
            ML_rView.visibility = View.VISIBLE
            startText.visibility = View.GONE
        }
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

        for (T_currency in curResults) {
            currencyAdapter.add(T_currency.code + " - " + T_currency.name)
        }
        currencyAdapter.add("추가하기")
    }


    fun initListener() {
        MLAdapter.itemClickListener = object : MainListAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
                val MLIntent = Intent(applicationContext, PickDateActivity::class.java)
                MLIntent.putExtra("ListID", data.id)
                MLIntent.putExtra("listPos", position)
                startActivityForResult(MLIntent, REQUEST_CODE)
            }
            override fun OnEditClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
//                val builder = AlertDialog.Builder(this@MainListActivity) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
//                val addListDialog = layoutInflater.inflate(konkukSW.MP2019.roadline.R.layout.add_list_dialog, null)
//                val addListText = addListDialog.findViewById<TextView>(konkukSW.MP2019.roadline.R.id.AL_text)
//                val addListTitle = addListDialog.findViewById<EditText>(konkukSW.MP2019.roadline.R.id.AL_title)
//                addListText.text = "여행 수정"
//                val item = listResults[position]
//                addListTitle.setText(item!!.title)
//
//                val editStart = addListDialog.findViewById<TextView>(R.id.editStart)
//                val editEnd = addListDialog.findViewById<TextView>(R.id.editEnd)
//
//                val curTextArray = arrayListOf<TextView>()
//                curTextArray.add(addListDialog.findViewById(R.id.curText0))
//                curTextArray.add(addListDialog.findViewById(R.id.curText1))
//                curTextArray.add(addListDialog.findViewById(R.id.curText2))
//                curTextArray.add(addListDialog.findViewById(R.id.curText3))
//                curTextArray.add(addListDialog.findViewById(R.id.curText4))
//                val curArray = arrayListOf<T_Currency>()
//                val currencySpinner = addListDialog.findViewById<Spinner>(R.id.currencySpinner)
//                currencySpinner.adapter = currencyAdapter
//                currencySpinner.setSelection(currencyAdapter.count)
//                for(i in 0 until item.currencys.size){
//                    curArray.add(item.currencys[i]!!)
//                    curTextArray[i].text = item.currencys[i]!!.code
//                    curTextArray[i].visibility = View.VISIBLE
//                }
//                currencySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                        currencySpinner.setSelection(currencyAdapter.count)
//                        if(position < currencySpinner.count){
//                            val result = parent!!.getItemAtPosition(position).toString()
//                            val code = result.split(" - ")[0].trim()
//
//                            val curTuple = curResults.where().equalTo("code", code).findFirst()
//                            var exist = false
//                            for(i in curArray){
//                                if(curTuple!!.code == i.code){
//                                    exist = true
//                                }
//                            }
//                            if(curArray.size < CURRENCY_MAX_SIZE){
//                                if(exist){
//                                    Toast.makeText(applicationContext, "이미 선택한 화폐입니다", Toast.LENGTH_SHORT).show()
//                                }
//                                else{
//                                    curArray.add(curTuple!!)
//                                    val index = curArray.size - 1
//                                    curTextArray[index].visibility = View.VISIBLE
//                                    curTextArray[index].text = curTuple!!.code
//                                }
//                            }
//                            else{
//                                Toast.makeText(applicationContext, "화폐는 5개까지 설정할 수 있습니다 ", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>?) {
//                    }
//                }
//
//
//                curTextArray[0].setOnLongClickListener{
//                    Toast.makeText(applicationContext, "한화는 기본 값으로 삭제할수 없습니다", Toast.LENGTH_SHORT).show()
//                    false
//                }
//
//                for(i in 1 until CURRENCY_MAX_SIZE){
//                    curTextArray[i].setOnLongClickListener {
//                        val ynBuilder = AlertDialog.Builder(this@MainListActivity)
//                        ynBuilder.setMessage(curTextArray[i].text.toString() + " 화폐를 삭제하시겠습니까?")
//                                .setPositiveButton("삭제") { dialogInterface, _ ->
//                                    Log.d("mytag", "cur longclick")
//                                    val index = curArray.size - 1
//                                    curArray.removeAt(i)
//                                    curTextArray[index].visibility = View.INVISIBLE
//                                    curTextArray[index].text = ""
//                                    for(j in 1 until curArray.size){
//                                        curTextArray[j].text = curArray[j].code
//                                    }
//                                }
//                                .setNegativeButton("취소") { dialogInterface, i ->
//                                }
//                        val ynDialog = ynBuilder.create()
//                        ynDialog.show()
//
//                        false
//                    }
//                }
//
//                var dateStart = item!!.dateStart.toInstant().atZone(org.threeten.bp.ZoneId.systemDefault()).toLocalDate()
//                var dateEnd = item!!.dateEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
//
//                editStart.setText(dateStart.format(org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//                editEnd.setText(dateEnd.format(org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//
//                val startListener = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
//                    dateStart = org.threeten.bp.LocalDate.of(year, month + 1, dayOfMonth)
//                    editStart.setText(dateStart.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//                }
//
//                val endListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
//                    dateEnd = LocalDate.of(year, month + 1, dayOfMonth)
//                    editEnd.setText(dateEnd.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
//                }
//
//                editStart.setOnClickListener {
//                    var dialog = DatePickerDialog(this@MainListActivity, startListener, dateStart.year, dateStart.monthValue - 1, dateStart.dayOfMonth)
//                    dialog.show()
//                }
//
//                editEnd.setOnClickListener {
//                    var dialog = DatePickerDialog(this@MainListActivity, endListener, dateEnd.year, dateEnd.monthValue - 1, dateEnd.dayOfMonth)
//                    val c = Calendar.getInstance()
//                    c.set(dateStart!!.year, dateStart!!.monthValue - 1, dateStart!!.dayOfMonth)
//                    dialog.datePicker.minDate = c.timeInMillis
//                    dialog.show()
//                }
//
//                builder.setView(addListDialog)
//                    .setPositiveButton("수정") { dialogInterface, _ ->
//                    }
//                    .setNegativeButton("취소") { dialogInterface, i ->
//                    }
//
//                val cbuilder = builder.create()
//                cbuilder.setCanceledOnTouchOutside(false)
//                cbuilder.show()
//
//                cbuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
//                    if(addListTitle.text.trim().toString() == ""){
//                        Toast.makeText(applicationContext, "여행 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
//                    }
//                    else {
//                        if (dateStart != null && dateEnd != null) {
//                            if (dateStart!!.isBefore(dateEnd) || dateStart!!.isEqual(dateEnd)) {
//                                realm.beginTransaction()
//                                item.title = addListTitle.text.toString()
//                                item.dateStart = java.util.Date.from(dateStart!!.atStartOfDay(ZoneId.systemDefault()).toInstant())
//                                item.dateEnd = java.util.Date.from(dateEnd!!.atStartOfDay(ZoneId.systemDefault()).toInstant())
//                                item.currencys.clear()
//                                for(i in curArray){
//                                    item.currencys.add(i)
//                                }
//                                realm.where(T_Day::class.java).equalTo("listID", item.id).findAll().deleteAllFromRealm()
//                                val pnum = (dateStart.until(dateEnd, ChronoUnit.DAYS) + 1).toInt()
//                                for (i in 1..pnum) {
//                                    val newDay = realm.createObject(T_Day::class.java)
//                                    newDay.listID = item.id
//                                    newDay.date = java.util.Date.from(dateStart!!.plusDays((i - 1).toLong()).atStartOfDay(ZoneId.systemDefault()).toInstant())
//                                    newDay.num = i
//                                }
//                                realm.commitTransaction()
//                                cbuilder.dismiss()
//                            } else {
//                                Toast.makeText(applicationContext, "종료일이 시작일보다 이전일수 없습니다", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            Toast.makeText(applicationContext, "시작일과 종료일 모두 입력해주세요", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
            }
            override fun OnDeleteClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
                val builder = AlertDialog.Builder(this@MainListActivity) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
                val deleteListDialog =
                    layoutInflater.inflate(R.layout.delete_list_dialog, null)
                val deleteListText = deleteListDialog.findViewById<TextView>(R.id.DL_textView)
                val item = listResults[position]
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
                        if(listResults.size == 0) {
                            ML_rView.visibility = View.GONE
                            startText.visibility = View.VISIBLE
                        } else {
                            ML_rView.visibility = View.VISIBLE
                            startText.visibility = View.GONE
                        }
                    }
                    .setNegativeButton("취소") { dialogInterface, i ->

                    }
                    .show()
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
            val korCur = curResults.where().equalTo("code", "KRW").findFirst()!!
            curArray.add(korCur)
            currencySpinner.setSelection(currencyAdapter.count)
            currencySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    currencySpinner.setSelection(currencyAdapter.count)
                    if(position < currencySpinner.count){
                        val result = parent?.getItemAtPosition(position).toString()
                        val code = result.split(" - ")[0].trim()

                        val curTuple = curResults.where().equalTo("code", code).findFirst()
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

//            var dateStart: Any? = null
//            var dateEnd:Any? = null

            var dateStartEpoch: Long? = null
            var dateEndEpoch: Long? = null
            var dateStartYear: Int? = null
            var dateStartMonth :Int? = null
            var dateStartDay: Int? = null
            var dateEndYear :Int? = null
            var dateEndMonth :Int? = null
            var dateEndDay :Int? = null


            val startListener = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                dateStartYear=  year
                dateStartMonth = month + 1
                dateStartDay = dayOfMonth
                if(android.os.Build.VERSION.SDK_INT >= 26) {
                    val dateStart = LocalDate.of(year, month + 1, dayOfMonth)
                    editStart.text = (dateStart as LocalDate).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    dateStartEpoch = dateStart.toEpochDay()
                    Log.d("mytest", dateStartEpoch.toString())
                }
                else{
                    val dateStart = org.threeten.bp.LocalDate.of(year, month + 1, dayOfMonth)
                    editStart.text = (dateStart as org.threeten.bp.LocalDate).format(org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    dateStartEpoch = dateStart.toEpochDay()
                    Log.d("mytest", dateStartEpoch.toString())
                }
            }

            val endListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                dateEndYear=  year
                dateEndMonth = month + 1
                dateEndDay = dayOfMonth
                if(android.os.Build.VERSION.SDK_INT >= 26) {
                    val dateEnd = LocalDate.of(year, month + 1, dayOfMonth)
                    editEnd.text = (dateEnd as LocalDate).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    dateEndEpoch = dateEnd.toEpochDay()
                }
                else{
                    val dateEnd = org.threeten.bp.LocalDate.of(year, month + 1, dayOfMonth)
                    editEnd.text = (dateEnd as org.threeten.bp.LocalDate).format(org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                    dateEndEpoch = dateEnd.toEpochDay()
                    Log.d("mytest", dateEndEpoch.toString())
                }

            }

            editStart.setOnClickListener {
                var dialog:DatePickerDialog
                if(dateStartEpoch == null) {
                    dialog = DatePickerDialog(
                            this@MainListActivity,
                            startListener, nowYear, nowMonth, nowDay)
                }
                else {
                    dialog = DatePickerDialog(this@MainListActivity, startListener, dateStartYear!!, dateStartMonth!! - 1, dateStartDay!!)
                }
                dialog.show()
            }

            editEnd.setOnClickListener {
                var dialog:DatePickerDialog
                if(dateEndEpoch == null) {
                    if (dateStartEpoch != null) {
                        val c = Calendar.getInstance()
                        dialog = DatePickerDialog(
                                this@MainListActivity,
                                endListener,
                                dateStartYear!!,
                                dateStartMonth!! - 1,
                                dateStartDay!!
                        )
                        c.set(
                                dateStartYear!!,
                                dateStartMonth!! - 1,
                                dateStartDay!!
                        )
                        dialog.datePicker.minDate = c.timeInMillis
                    }
                    else{
                        dialog = DatePickerDialog(this@MainListActivity, endListener, nowYear, nowMonth,nowDay)
                    }
                }
                else {
                    dialog = DatePickerDialog(this@MainListActivity, endListener, dateEndYear!!, dateEndMonth!! - 1, dateEndDay!!)
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
                    if(dateStartEpoch != null && dateEndEpoch != null) {
                        if(dateStartEpoch!! <= dateEndEpoch!!){
                            realm.beginTransaction()
                            val newList = realm.createObject(T_List::class.java, UUID.randomUUID().toString())
                            newList.title = addListTitle.text.toString()
                            newList.dateStart = dateStartEpoch!!
                            newList.dateEnd = dateEndEpoch!!

                            for(i in curArray){
                                newList.currencys.add(i)
                            }
                            realm.commitTransaction()
                            val pnum = dateEndEpoch!! - dateStartEpoch!! + 1
                            for(i in 1..pnum){
                                realm.beginTransaction()
                                val newDay  = realm.createObject(T_Day::class.java)
                                newDay.listID = newList.id
                                newDay.date = dateStartEpoch!! + i - 1
                                newDay.num = i.toInt()
                                realm.commitTransaction()
                            }

                            if(listResults.size == 0) {
                                ML_rView.visibility = View.GONE
                                startText.visibility = View.VISIBLE
                            } else {
                                ML_rView.visibility = View.VISIBLE
                                startText.visibility = View.GONE
                            }
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

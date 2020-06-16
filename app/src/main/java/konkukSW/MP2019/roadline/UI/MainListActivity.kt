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

    //여행추가 dialog
    lateinit var builder:AlertDialog.Builder
    lateinit var addListDialog: View
    lateinit var addListTitle: EditText
    lateinit var editStart: TextView
    lateinit var editEnd: TextView
    val curTextArray = arrayListOf<TextView>()
    lateinit var addListText: TextView

    val curArray = arrayListOf<T_Currency>() //리스트 마다 dialog 내부의 화폐 종류
    lateinit var currencySpinner:Spinner
    lateinit var korCur: T_Currency
    var dateStartEpoch: Long? = null
    var dateEndEpoch: Long? = null


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
        initDialog()
        initListener()
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

    fun initDialog(){
        var dateStartYear: Int? = null
        var dateStartMonth :Int? = null
        var dateStartDay: Int? = null
        var dateEndYear :Int? = null
        var dateEndMonth :Int? = null
        var dateEndDay :Int? = null

        builder = AlertDialog.Builder(this) //여행 추가 dialog
        addListDialog = layoutInflater.inflate(R.layout.add_list_dialog, null)

        addListText = addListDialog.findViewById(R.id.AL_text)
        addListTitle = addListDialog.findViewById(R.id.AL_title)
        editStart = addListDialog.findViewById(R.id.editStart)
        editEnd = addListDialog.findViewById(R.id.editEnd)

        curTextArray.add(addListDialog.findViewById(R.id.curText0))
        curTextArray.add(addListDialog.findViewById(R.id.curText1))
        curTextArray.add(addListDialog.findViewById(R.id.curText2))
        curTextArray.add(addListDialog.findViewById(R.id.curText3))
        curTextArray.add(addListDialog.findViewById(R.id.curText4))

        currencySpinner = addListDialog.findViewById(R.id.currencySpinner)
        currencySpinner.adapter = currencyAdapter
        korCur = curResults.where().equalTo("code", "KRW").findFirst()!!

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

        val startListener = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            dateStartYear=  year
            dateStartMonth = month + 1
            dateStartDay = dayOfMonth
            if(android.os.Build.VERSION.SDK_INT >= 26) {
                val dateStart = LocalDate.of(year, month + 1, dayOfMonth)
                editStart.text = (dateStart as LocalDate).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                dateStartEpoch = dateStart.toEpochDay()
            }
            else{
                val dateStart = org.threeten.bp.LocalDate.of(year, month + 1, dayOfMonth)
                editStart.text = (dateStart as org.threeten.bp.LocalDate).format(org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                dateStartEpoch = dateStart.toEpochDay()
            }
        }

        val endListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            dateEndYear = year
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
                //nullpointerror
                val date = org.threeten.bp.LocalDate.ofEpochDay(dateStartEpoch!!)
                dialog = DatePickerDialog(this@MainListActivity, startListener, date.year, date.monthValue - 1, date.dayOfMonth)
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
                val date = org.threeten.bp.LocalDate.ofEpochDay(dateEndEpoch!!)
                dialog = DatePickerDialog(this@MainListActivity, endListener, date.year, date.monthValue - 1, date.dayOfMonth)
            }
            dialog.show()
        }

    }


    fun initListener() {
        ML_addListBtn.setOnClickListener {
            addListText.text = "여행 추가"

            if(addListDialog.parent != null){
                (addListDialog.parent as ViewGroup).removeView(addListDialog)
            }
            builder.setView(addListDialog)
                    .setPositiveButton("추가") { _, _ -> }
                    .setNegativeButton("취소") { _, _ -> }
            
            dateStartEpoch = null
            dateEndEpoch = null

            curArray.clear()
            curArray.add(korCur)

            val cbuilder = builder.create() //여행추가 다이얼로그
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

        MLAdapter.itemClickListener = object : MainListAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
                val MLIntent = Intent(applicationContext, PickDateActivity::class.java)
                MLIntent.putExtra("ListID", data.id)
                MLIntent.putExtra("listPos", position)
                startActivityForResult(MLIntent, REQUEST_CODE)
            }

            override fun OnDeleteClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
                val deleteListDialog =
                        layoutInflater.inflate(R.layout.delete_list_dialog, null)
                val deleteListText = deleteListDialog.findViewById<TextView>(R.id.DL_textView)
                val item = listResults[position]
                var deleteMessage = "'"+item!!.title+deleteListText.text
                deleteListText.text = deleteMessage

                if(deleteListDialog.parent != null){
                    (deleteListDialog.parent as ViewGroup).removeView(deleteListDialog)
                }
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



            override fun OnEditClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
                if(addListDialog.parent != null){
                    (addListDialog.parent as ViewGroup).removeView(addListDialog)
                }
                builder.setView(addListDialog)
                        .setPositiveButton("수정") { _, _ -> }
                        .setNegativeButton("취소") { _, _ -> }

                addListText.text = "여행 수정"

                val item = listResults[position]!!
                addListTitle.setText(item!!.title)
                curArray.clear()
                for(i in 0 until item.currencys.size){
                    curArray.add(item.currencys[i]!!)
                    curTextArray[i].text = item.currencys[i]!!.code
                    curTextArray[i].visibility = View.VISIBLE
                }

                dateStartEpoch = item.dateStart
                dateEndEpoch = item.dateEnd

                if(android.os.Build.VERSION.SDK_INT >= 26) {
                    val dateFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    val dateStart = LocalDate.ofEpochDay(dateStartEpoch!!)
                    val dateEnd = LocalDate.ofEpochDay(dateEndEpoch!!)
                    editStart.text = dateStart.format(dateFormat)
                    editEnd.text = dateEnd.format(dateFormat)
                }
                else{
                    val dateForamt = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    val dateStart = org.threeten.bp.LocalDate.ofEpochDay(dateStartEpoch!!)
                    val dateEnd = org.threeten.bp.LocalDate.ofEpochDay(dateEndEpoch!!)
                    editStart.text = dateStart.format(dateForamt)
                    editEnd.text = dateEnd.format(dateForamt)
                }



                val cbuilder = builder.create()
                cbuilder.setCanceledOnTouchOutside(false)
                cbuilder.show()

                cbuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if(addListTitle.text.trim().toString() == ""){
                        Toast.makeText(applicationContext, "여행 제목을 입력해주세요", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        if (dateStartEpoch != null && dateEndEpoch != null) {
                            if (dateStartEpoch!! <= dateEndEpoch!!) {
                                realm.beginTransaction()
                                item.title = addListTitle.text.toString()
                                item.dateStart = dateStartEpoch!!
                                item.dateEnd = dateEndEpoch!!
                                item.currencys.clear()
                                for(i in curArray){
                                    item.currencys.add(i)
                                }
                                realm.commitTransaction()
                                realm.beginTransaction()
                                realm.where(T_Day::class.java).equalTo("listID", item.id).findAll().deleteAllFromRealm()
                                realm.commitTransaction()
                                val pnum = dateEndEpoch!! - dateStartEpoch!! + 1
                                for (i in 1..pnum) {
                                    realm.beginTransaction()
                                    val newDay = realm.createObject(T_Day::class.java)
                                    newDay.listID = item.id
                                    newDay.date = dateStartEpoch!! + i -1
                                    newDay.num = i.toInt()
                                    realm.commitTransaction()
                                }
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


        }


    }
}

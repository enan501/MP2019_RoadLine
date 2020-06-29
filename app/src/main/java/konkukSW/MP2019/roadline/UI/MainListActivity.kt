package konkukSW.MP2019.roadline.UI

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.DialogTitle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where
import konkukSW.MP2019.roadline.Data.Adapter.*
import konkukSW.MP2019.roadline.Data.DB.*
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.PickDateActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import konkukSW.MP2019.roadline.UI.widget.AddListDialog
import konkukSW.MP2019.roadline.UI.widget.BaseDialog
import konkukSW.MP2019.roadline.UI.widget.ProgressDialog
import konkukSW.MP2019.roadline.refreshCurrency
import kotlinx.android.synthetic.main.activity_main_list.*
import kotlinx.android.synthetic.main.activity_show_money.*
import kotlinx.android.synthetic.main.add_list_dialog.*
import kotlinx.android.synthetic.main.add_list_dialog.view.*
import kotlinx.android.synthetic.main.grid_image_item.*
import kotlinx.android.synthetic.main.image_pick_dialog.*
import kotlinx.android.synthetic.main.setting_header.view.*
import org.w3c.dom.Text
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList


class MainListActivity : AppCompatActivity() {


    lateinit var MLAdapter:MainListAdapter
    lateinit var currencyAdapter: ArrayAdapter<String>
    lateinit var selectedCurrencyAdapter: CurrencyAdapter
    lateinit var realm:Realm
    private val REQUEST_CODE = 123
    lateinit var listResults: RealmResults<T_List>
    lateinit var curResults: RealmResults<T_Currency>
    var clickedPhoto : T_Photo? = null
    lateinit var photoResults: RealmResults<T_Photo>
    var nowYear = 0
    var nowMonth = 0
    var nowDay = 0

    //여행추가 dialog
    lateinit var builder:AddListDialog.Builder
    lateinit var addListDialog: View
    lateinit var addListTitle: EditText
    lateinit var editStart: TextView
    lateinit var editEnd: TextView
    lateinit var imageView: ImageView
    lateinit var imageViewBack: TextView
    lateinit var textViewTitle: TextView

    val curArray = arrayListOf<T_Currency>() //리스트 마다 dialog 내부의 화폐 종류
    lateinit var currencySpinner: SearchableSpinner
    lateinit var korCur: T_Currency
    var dateStartEpoch: Long? = null
    var dateEndEpoch: Long? = null
    lateinit var imm :InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)
        Realm.init(this)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE) {
                if(photoResults.isNotEmpty()){
                    imageViewBack.text = "이곳을 눌러 사진을 선택하세요"
            }
        }
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

        if(Build.VERSION.SDK_INT >= 26) {
            nowYear = LocalDate.now().year
            nowMonth = LocalDate.now().monthValue - 1
            nowDay = LocalDate.now().dayOfMonth
        }
        else{
            nowYear =org.threeten.bp.LocalDate.now().year
            nowMonth = org.threeten.bp.LocalDate.now().monthValue - 1
            nowDay = org.threeten.bp.LocalDate.now().dayOfMonth
        }
        korCur = curResults.where().equalTo("code", "KRW").findFirst()!!
    }

    fun initLayout(){
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                convertView.textSize = 12f
                return convertView
            }

            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }

        for (T_currency in curResults) {
            currencyAdapter.add(T_currency.code + " - " + T_currency.name)
        }
        currencyAdapter.add("추가하기")

        /* --------- selected currency Adapter --------- */
        curArray.add(korCur)
        selectedCurrencyAdapter = CurrencyAdapter(curArray, this)
        selectedCurrencyAdapter.itemClickListener = object:CurrencyAdapter.OnItemClickListener{
            override fun OnLongClick(position: Int): Boolean {
                if(position == 0){
                    val builder = BaseDialog.Builder(this@MainListActivity).create()
                    builder.setTitle("알림")
                            .setMessage("한화는 기본 값으로 삭제할수 없습니다")
                            .setCancelButton("확인")
                            .show()
                    return false
                } else {
                    val builder = BaseDialog.Builder(this@MainListActivity).create()
                    builder.setTitle("알림")
                            .setMessage(curArray[position].name + " 화폐를 삭제하시겠습니까?")
                            .setOkButton("삭제", View.OnClickListener {
                                curArray.removeAt(position)
                                selectedCurrencyAdapter.notifyItemRemoved(position)
                                builder.dismissDialog()
                            })
                            .setCancelButton("취소")
                            .show()

                    return false
                }
            }
        }

        //cellspacing?
    }

    fun initDialog(){
        var dateStartYear: Int? = null
        var dateStartMonth :Int? = null
        var dateStartDay: Int? = null
        var dateEndYear :Int? = null
        var dateEndMonth :Int? = null
        var dateEndDay :Int? = null
        builder = AddListDialog.Builder(this).create() //여행 추가 dialog
        currencySpinner = builder.dialog.AL_currencySpinner
        editStart = builder.dialog.editStart
        editEnd = builder.dialog.editEnd
        addListTitle = builder.dialog.AL_title
        textViewTitle = builder.dialog.textViewTitle
        imageView = builder.dialog.imageView
        imageViewBack = builder.dialog.imageViewBack
        builder.setCurrencyAdapter(currencyAdapter)
                .setSelectedCurrencyAdapter(selectedCurrencyAdapter)
        if(Build.VERSION.SDK_INT >= 26) {
            currencySpinner.setAutofillHints("화폐를 검색하세요")
        }
        else{
            currencySpinner.setTitle("")
        }
        currencySpinner.setPositiveButton("취소")


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
                    if(exist){
                        val builder = BaseDialog.Builder(this@MainListActivity).create()
                        builder.setTitle("알림")
                                .setMessage("이미 선택한 화폐입니다")
                                .setCancelButton("확인")
                                .show()
                    }
                    else{
                        curArray.add(curTuple!!)
                        selectedCurrencyAdapter.notifyItemInserted(curArray.size-1)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }


        val startListener = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
            dateStartYear=  year
            dateStartMonth = month + 1
            dateStartDay = dayOfMonth
            if(Build.VERSION.SDK_INT >= 26) {
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
            if(Build.VERSION.SDK_INT >= 26) {
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
        settingView.itemHorizontalPadding = resources.getDimensionPixelSize(R.dimen.setting_padding)
    }


    fun initListener() {
        btnSetting.setOnClickListener { drawerLayout.openDrawer(settingView)  }

//        settingView.getHeaderView(0).btnClose.setOnClickListener {
//            drawerLayout.closeDrawer(settingView)
//        }
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener{ appBarLayout: AppBarLayout?, verticalOffset: Int ->
            if(verticalOffset <0) ML_subTitle.visibility = View.GONE
            else ML_subTitle.visibility = View.VISIBLE
        })
        settingView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.btnRefreshCurrency->{
                    refreshCurrency(this)
                    drawerLayout.closeDrawer(settingView)
                }
                R.id.btnRate -> {
                    try {
                        val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                        startActivity(intent)
                    }
                    drawerLayout.closeDrawer(settingView)
                }
                R.id.btnVersionInfo -> {
                    var dialog = BaseDialog.Builder(this@MainListActivity).create()
                    dialog.setTitle("버젼 정보")
                            .setMessage("Ver 1.0.0")
                            .setOkButton("닫기", View.OnClickListener { dialog.dismissDialog() })
                            .show()
                }
            }
            true
        }

        ML_addListBtn.setOnClickListener {
            addListTitle.text.clear()
            editStart.text = "시작일 입력하기"
            editEnd.text = "종료일 입력하기"
            textViewTitle.visibility = View.GONE
            imageView.visibility = View.GONE
            imageViewBack.visibility = View.GONE

            dateStartEpoch = null
            dateEndEpoch = null


            curArray.clear()
            curArray.add(korCur)


            builder.setCancelButton("취소")
                    .setOkButton("확인", View.OnClickListener{
                if(addListTitle.text.trim().toString() == ""){
                    val builder = BaseDialog.Builder(this@MainListActivity).create()
                    builder.setTitle("알림")
                            .setMessage("여행 제목을 입력해주세요")
                            .setCancelButton("확인")
                            .show()
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
                            builder.dismissDialog()
                        }
                        else{
                            val builder = BaseDialog.Builder(this@MainListActivity).create()
                            builder.setTitle("알림")
                                    .setMessage("종료일이 시작일보다 이전일수 없습니다")
                                    .setCancelButton("확인")
                                    .show()
                        }
                    }
                    else{
                        val builder = BaseDialog.Builder(this@MainListActivity).create()
                        builder.setTitle("알림")
                                .setMessage("시작일과 종료일을 입력해주세요")
                                .setCancelButton("확인")
                                .show()
                    }
                }
            }
            ).show()

            imm.hideSoftInputFromWindow(addListTitle.windowToken, 0)

        }

        MLAdapter.itemClickListener = object : MainListAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
                val MLIntent = Intent(this@MainListActivity, PickDateActivity::class.java)
                MLIntent.putExtra("ListID", data.id)
                MLIntent.putExtra("backgroundImg", data.img)
                MLIntent.putExtra("listPos", position)
                startActivity(MLIntent)
            }

            override fun OnDeleteClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
                val item = listResults[position]
                val builder = BaseDialog.Builder(this@MainListActivity).create()
                builder.setTitle("알림")
                        .setMessage("'" + item!!.title + "' 기록을 삭제할까요?")
                        .setOkButton("삭제", View.OnClickListener {
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
                            builder.dismissDialog()
                        })
                        .setCancelButton("취소")
                        .show()

            }

            override fun OnEditClick(holder: MainListAdapter.ViewHolder, data: T_List, position: Int) {
                photoResults = realm.where(T_Photo::class.java).equalTo("listID", data.id).findAll().sort("dayNum", Sort.ASCENDING, "dateTime", Sort.ASCENDING)

                val item = listResults[position]!!
                addListTitle.setText(item.title)
                curArray.clear()
                curArray.addAll(data.currencys)

                textViewTitle.visibility = View.VISIBLE


                dateStartEpoch = item.dateStart
                dateEndEpoch = item.dateEnd

                imageViewBack.setOnClickListener {
                    if(photoResults.isNotEmpty()){
                        showImagePickDialog()
                    }
                    else{
                        //추억함으로 이동
                        var intent = Intent(this@MainListActivity, ShowPhotoActivity::class.java)
                        intent.putExtra("ListID", data.id)
                        intent.putExtra("DayNum",0)
                        startActivityForResult(intent, REQUEST_CODE)
                        overridePendingTransition(
                                R.anim.anim_slide_in_top,
                                R.anim.anim_slide_out_bottom
                        )
                    }
                }

                if(item.img == ""){
                    imageView.visibility = View.INVISIBLE
                    imageViewBack.visibility = View.VISIBLE
                    if(photoResults.isEmpty()){
                        imageViewBack.text = "이곳을 눌러 추억함에 사진을 추가하세요"
                    }
                    else{
                        imageViewBack.text = "이곳을 눌러 사진을 선택하세요"
                    }
                }
                else{
                    imageView.visibility = View.VISIBLE
                    imageViewBack.visibility = View.INVISIBLE
                    Glide.with(applicationContext).load(item.img).into(imageView)
                }



                if(Build.VERSION.SDK_INT >= 26) {
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

                builder.setCanceledOnTouchOutside(false)
                        .setCancelButton("취소", View.OnClickListener {
                            holder.edit.visibility = View.INVISIBLE
                            holder.delete.visibility = View.INVISIBLE
                            builder.dismissDialog()
                        })
                        .setOkButton("수정", View.OnClickListener {
                    if(addListTitle.text.trim().toString() == ""){
                        val builder = BaseDialog.Builder(this@MainListActivity).create()
                        builder.setTitle("알림")
                                .setMessage("여행 제목을 입력해주세요")
                                .setCancelButton("확인")
                                .show()
                    }
                    else {
                        if (dateStartEpoch != null && dateEndEpoch != null) {
                            if (dateStartEpoch!! <= dateEndEpoch!!) {
                                realm.beginTransaction()
                                item.title = addListTitle.text.toString()
                                item.dateStart = dateStartEpoch!!
                                item.dateEnd = dateEndEpoch!!
                                if(clickedPhoto != null){
                                    item.img = clickedPhoto!!.img
                                }
                                item.currencys.clear()
                                for(i in curArray){
                                    item.currencys.add(i)
                                }
                                realm.commitTransaction()
                                val pnum = dateEndEpoch!! - dateStartEpoch!! + 1

                                var dayList = realm.where(T_Day::class.java).equalTo("listID", item.id).sort("num").findAll()!!
                                var imgList = ArrayList<String?>()
                                for(i in 0 until dayList.size){
                                    imgList.add(dayList[i]!!.img)
                                }
                                realm.beginTransaction()
                                dayList.deleteAllFromRealm()
                                realm.commitTransaction()
                                for (i in 1..pnum) {
                                    realm.beginTransaction()
                                    val newDay = realm.createObject(T_Day::class.java)
                                    newDay.listID = item.id
                                    newDay.date = dateStartEpoch!! + i -1
                                    newDay.num = i.toInt()
                                    if((i - 1).toInt() < imgList.size)
                                        newDay.img = imgList[(i - 1).toInt()]
                                    realm.commitTransaction()
                                }

                                builder.dismissDialog()
                            } else {
                                val builder = BaseDialog.Builder(this@MainListActivity).create()
                                builder.setTitle("알림")
                                        .setMessage("종료일이 시작일보다 이전일수 없습니다")
                                        .setCancelButton("확인")
                                        .show()
                            }
                        }
                        else{
                            val builder = BaseDialog.Builder(this@MainListActivity).create()
                            builder.setTitle("알림")
                                    .setMessage("시작일과 종료일을 입력해주세요")
                                    .setCancelButton("확인")
                                    .show()
                        }
                    }
                }).show()
                imageView.setOnClickListener {
                    showImagePickDialog()
                }
                imm.hideSoftInputFromWindow(addListTitle.windowToken, 0)
            }
        }
    }

    fun showImagePickDialog(){
        clickedPhoto = null
        val imagePickBuilder = AlertDialog.Builder(this@MainListActivity)
        val imagePickView = layoutInflater.inflate(R.layout.image_pick_dialog, null)
        val pickImageView = imagePickView.findViewById<RecyclerView>(R.id.rView)
        val backTextView = imagePickView.findViewById<TextView>(R.id.backTextView)
        var photoAdapter: PhotoPickGridAdapter?
        var isAvail = false
        pickImageView.setHasFixedSize(true)

        if(photoResults.size == 0){
            backTextView.visibility = View.VISIBLE
            pickImageView.visibility = View.INVISIBLE
        }
        else{
            isAvail = true
            backTextView.visibility = View.INVISIBLE
            pickImageView.visibility = View.VISIBLE
            photoAdapter = PhotoPickGridAdapter(photoResults, this@MainListActivity)
            pickImageView.adapter = photoAdapter
            pickImageView.layoutManager = GridLayoutManager(this@MainListActivity, 3)

            photoAdapter.itemClickListener = object : PhotoPickGridAdapter.OnItemClickListener{
                override fun onItemClick(
                        data: T_Photo,
                        clickedPos: Int
                ) {
                    clickedPhoto = data
                    if(clickedPos != -1){
                        val pickedImageView = pickImageView.findViewHolderForAdapterPosition(clickedPos)
                        if(pickedImageView != null){
                            (pickedImageView as PhotoPickGridAdapter.ViewHolder).backImage.visibility = View.INVISIBLE
                        }
                    }
                }

                override fun onNothingClicked() {
                    clickedPhoto = null
                }
            }
        }

        imagePickBuilder.setView(imagePickView)
        if(isAvail) {
            imagePickBuilder.setPositiveButton("추가"){_, _->}
                    .setNegativeButton("취소"){_, _->}
        }
        else{
            imagePickBuilder.setPositiveButton("확인"){_, _->}
        }
        val createdBuilder = imagePickBuilder.create() //여행추가 다이얼로그
        createdBuilder.setCanceledOnTouchOutside(false)
        createdBuilder.show()

        if(isAvail){
            createdBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if(clickedPhoto == null){
                        val builder = BaseDialog.Builder(this@MainListActivity).create()
                        builder.setTitle("알림")
                                .setMessage("사진을 선택해주세요")
                                .setCancelButton("확인")
                                .show()
                }
                else{
                    createdBuilder.dismiss()
                    imageView.visibility = View.VISIBLE
                    imageViewBack.visibility = View.INVISIBLE
                    Glide.with(applicationContext).load(clickedPhoto!!.img).into(imageView)
                }
            }
        }
    }


}

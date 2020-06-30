package konkukSW.MP2019.roadline.UI.money

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import konkukSW.MP2019.roadline.Data.DB.*
import konkukSW.MP2019.roadline.Extension.getPathFromUri
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.widget.AddPhotoDialog
import konkukSW.MP2019.roadline.UI.widget.BaseDialog
import kotlinx.android.synthetic.main.activity_add_money.*
import kotlinx.android.synthetic.main.activity_show_money.*
import kotlinx.coroutines.selects.select
import org.threeten.bp.LocalDate
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class AddMoneyActivity : AppCompatActivity() {
    val SELECT_IMAGE = 100
    val CAPTURE_IMAGE = 200
    var editMode = false
    var moneyId = ""
    var listID = ""
    var dayNum = 0
    var imgUri: String = "" // 이미지 URI
    var imgPath: String = ""//카메라 이미지 경로
    var cate: String = "" // 카테고리
    lateinit var realm:Realm
    lateinit var selectedCurrency: T_Currency
    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var curList:RealmList<T_Currency>
    var selectedMoney: T_Money? = null //수정모드일때만 초기화
    var exchange = 0.0
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money)
        Realm.init(this)
        init()
    }

    fun init() {
        initPermission()
        initData()
        initAdapter()
        initLayout()
        initListener()
    }

    fun initPermission() {
        if (!checkAppPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA))) {
            askPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), SELECT_IMAGE)
        }
    }

    fun checkAppPermission(requestPermission: Array<String>): Boolean {
        val requestResult = BooleanArray(requestPermission.size)
        for (i in requestResult.indices) {
            requestResult[i] = ContextCompat.checkSelfPermission(
                    this,
                    requestPermission[i]
            ) == PackageManager.PERMISSION_GRANTED
            if (!requestResult[i]) {
                return false
            }
        }
        return true
    }

    fun askPermission(requestPermission: Array<String>, REQ_PERMISSION: Int) {
        ActivityCompat.requestPermissions(
                this, requestPermission,
                REQ_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SELECT_IMAGE -> {
                if (checkAppPermission(permissions)) { //퍼미션 동의했을 때 할 일
                    Toast.makeText(this, "권한이 승인됨", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "권한이 거절됨", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            CAPTURE_IMAGE -> {
                if (checkAppPermission(permissions)) { //퍼미션 동의했을 때 할 일
                    Toast.makeText(this, "권한이 승인됨", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "권한이 거절됨", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initData(){
        realm = Realm.getDefaultInstance()
        val i = intent
        editMode = i.getBooleanExtra("editMode", false)
        dayNum = i.getIntExtra("DayNum", 0)
        listID = i.getStringExtra("ListID")
        if(editMode){ //수정
            moneyId = i.getStringExtra("moneyId")
            selectedMoney = realm.where(T_Money::class.java).equalTo("id", moneyId).findFirst()!!
            selectedCurrency = selectedMoney!!.currency!!
        }
        else{
            selectedCurrency = realm.where(T_Currency::class.java).equalTo("code", i.getStringExtra("cur")).findFirst()!!
        }
        
        curList = realm.where(T_List::class.java).equalTo("id", listID).findFirst()!!.currencys
    }


    fun initLayout(){
        for(i in 0 until curList.size){
            if(curList[i]!!.code == selectedCurrency.code){
                cSpinner.setSelection(i)
            }
        }

        if(editMode){
            am_toolbar.title = "가계부 수정"
            imgUri = selectedMoney!!.img
            if(imgUri == ""){
                addMoneyImage.setImageResource(R.drawable.photo_default)
            }
            else{
                Glide.with(applicationContext).load(selectedMoney!!.img).into(addMoneyImage)
            }
            val price = selectedMoney!!.price
            if(Math.floor(price) == price){
                priceTxt.setText(Math.floor(price).toInt().toString())
            }
            else{
                priceTxt.setText(price.toString())
            }
            memoText.setText(selectedMoney!!.memo)
            when(selectedMoney!!.cate){
                "식사" -> categoryGroup.check(R.id.mealBtn)
                "쇼핑" -> categoryGroup.check(R.id.shoppingBtn)
                "교통" -> categoryGroup.check(R.id.transportBtn )
                "관광" -> categoryGroup.check(R.id.tourBtn)
                "숙박" -> categoryGroup.check(R.id.lodgmentBtn)
                "기타" -> categoryGroup.check(R.id.etcBtn)
            }
            cate = selectedMoney!!.cate
        }
        else{
            am_toolbar.title = "가계부 추가"
        }
        setSupportActionBar(am_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun initAdapter(){
        spinnerAdapter = object :ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var convertView = convertView
                if(convertView == null){
                    convertView = LayoutInflater.from(this@AddMoneyActivity).inflate(android.R.layout.simple_spinner_item, null)
                }
                (convertView as TextView).text = curList[position]!!.symbol
                convertView.setTextColor(ContextCompat.getColor(this@AddMoneyActivity, R.color.colorPrimary))
                convertView.gravity = View.TEXT_ALIGNMENT_CENTER
                convertView.textSize = 20f
                return convertView
            }
        }
        for (i in curList) {
            spinnerAdapter.add(i.code + " - " + i.name)
        }
        cSpinner.adapter = spinnerAdapter
        cSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCurrency = curList[position]!!
                if(priceTxt.text.toString() != ""){
                    exchange = selectedCurrency.rate * priceTxt.text.toString().toDouble()
                    inputExchangeTextView(exchange)
                }
            }
        }
        if(spinnerAdapter.count == 1){
            cSpinner.isEnabled = false
        }
    }

    fun initListener(){
        categoryGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                when (checkedId) {
                    R.id.mealBtn -> cate = "식사"
                    R.id.shoppingBtn -> cate = "쇼핑"
                    R.id.transportBtn -> cate = "교통"
                    R.id.tourBtn -> cate = "관광"
                    R.id.lodgmentBtn -> cate = "숙박"
                    R.id.etcBtn -> cate = "기타"
                }
            }
        }

        addMoneyImage.setOnClickListener {


            val builder = AddPhotoDialog.Builder(this).create()
            builder.setAlbumButton(View.OnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                startActivityForResult(intent, SELECT_IMAGE)
                builder.dismissDialog()
            })
            builder.setCameraButton(View.OnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if(intent.resolveActivity(packageManager) != null){
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    }catch (e: IOException){}
                    if(photoFile != null){
                        val photoUri = FileProvider.getUriForFile(this, packageName + ".fileprovider", photoFile)
                        imgPath = photoFile.absolutePath
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(intent, CAPTURE_IMAGE)
                    }
                }
                builder.dismissDialog()
            })
            builder.setCanceledOnTouchOutside(true)
            builder.show()

        }

        priceTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "") {
                    addMoneyExchange.visibility = View.INVISIBLE
                } else {
                    addMoneyExchange.visibility = View.VISIBLE
                    exchange = selectedCurrency.rate * s.toString().toDouble()
                    inputExchangeTextView(exchange)
                }
            }
        })
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "TEST_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                imgUri = getPathFromUri(data!!.data)
                Glide.with(applicationContext).load(data.data).into(addMoneyImage)
            }
            else if(requestCode == CAPTURE_IMAGE){
                imgUri = imgPath
                Glide.with(applicationContext).load(imgUri).into(addMoneyImage)
            }
        }
    }


    fun getPathFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToNext()
        val path = cursor.getString(cursor.getColumnIndex("_data"))
        cursor.close()
        return path
    }

    fun submitBtn(view: View) {
        if(priceTxt.text.isEmpty() || cate.isEmpty()){
            var message = ""
            if(priceTxt.text.isEmpty()){
                message = "가격을 입력하세요"
            }
            else{
                message = "카테고리를 선택하세요"
            }
            val builder = BaseDialog.Builder(this@AddMoneyActivity).create()
            builder.setTitle("알림")
                    .setMessage(message)
                    .setCancelButton("확인")
                    .show()
        }
        else{
            if(editMode) { //수정
                realm.beginTransaction()
                selectedMoney!!.currency = selectedCurrency
                selectedMoney!!.img = imgUri
                selectedMoney!!.price = priceTxt.text.toString().toDouble() //원화 넣기
                selectedMoney!!.cate = cate
                if(android.os.Build.VERSION.SDK_INT >= 26) {
                    selectedMoney!!.dateTime = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond()
                }
                else{
                    selectedMoney!!.dateTime = org.threeten.bp.LocalDateTime.now().atZone(org.threeten.bp.ZoneId.of("Asia/Seoul")).toEpochSecond()
                }
                selectedMoney!!.memo = memoText.text.toString()
                realm.commitTransaction()

            }
            else{
                realm.beginTransaction()
                val moneyTable = realm.createObject(T_Money::class.java, UUID.randomUUID().toString())//데이터베이스에 저장할 객체 생성
                moneyTable.listID = listID
                moneyTable.dayNum = dayNum
                moneyTable.currency = selectedCurrency
                moneyTable.img = imgUri
                moneyTable.price = priceTxt.text.toString().toDouble() //원화 넣기
                moneyTable.cate = cate
                if(android.os.Build.VERSION.SDK_INT >= 26) {
                    moneyTable.dateTime = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond()
                }
                else{
                    moneyTable.dateTime = org.threeten.bp.LocalDateTime.now().atZone(org.threeten.bp.ZoneId.of("Asia/Seoul")).toEpochSecond()
                }
                moneyTable.memo = memoText.text.toString()
                realm.commitTransaction()
            }


            val item = realm.where(T_Photo::class.java).equalTo("listID", listID).equalTo("dayNum", dayNum).equalTo("img", imgUri).findFirst()
            if(imgUri != "" && item == null){
                realm.beginTransaction()
                val photoTable = realm.createObject(T_Photo::class.java, UUID.randomUUID().toString())
                photoTable.listID = listID
                photoTable.dayNum = dayNum
                photoTable.img = imgUri
                if(android.os.Build.VERSION.SDK_INT >= 26) {
                    photoTable.dateTime = LocalDate.now().toEpochDay()
                }
                else{
                    photoTable.dateTime = LocalDate.now().toEpochDay()
                }
                realm.commitTransaction()
            }
            finish()
        }
    }

    fun inputExchangeTextView(num:Double){
        if(num.roundToInt().toString().length >= 6){
            addMoneyExchange.text = shortFormat.format(num) + "원"
        }
        else{
            addMoneyExchange.text = longFormat.format(num) + "원"
        }
    }
}
package konkukSW.MP2019.roadline.UI.money

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
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
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_add_money.*
import kotlinx.android.synthetic.main.activity_show_money.*
import kotlinx.coroutines.selects.select
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class AddMoneyActivity : AppCompatActivity() {
    val SELECT_IMAGE = 100
    var listID = ""
    var dayNum = 0
    var img_url: String = "" // 이미지 URI
    var cate: String = "" // 카테고리
    var realm = Realm.getDefaultInstance()
    lateinit var selectedCurrency: T_Currency
    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var curList:RealmList<T_Currency>
    var exchange = 0.0
    var pos = -1
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
        if (!checkAppPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            askPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), SELECT_IMAGE)
        } else {
            Toast.makeText(applicationContext, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
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
            SELECT_IMAGE -> if (checkAppPermission(permissions)) { //퍼미션 동의했을 때 할 일
                Toast.makeText(this, "권한이 승인됨", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "권한이 거절됨", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            val builder = AlertDialog.Builder(this)
            builder.setMessage("지금 돌아가면 데이터 입력 내용이 삭제됩니다.")
                    .setTitle("뒤로가기")
                    .setIcon(konkukSW.MP2019.roadline.R.drawable.ic_keyboard_backspace_black_24dp)
            builder.setPositiveButton("OK") { _, _ ->
                finish()
            }
            val dialog = builder.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initData(){
        Realm.init(applicationContext)
        val i = intent
        pos = i.getIntExtra("pos", -1)
        dayNum = i.getIntExtra("DayNum", 0)
        listID = i.getStringExtra("ListID")
        selectedCurrency = realm.where(T_Currency::class.java).equalTo("code", i.getStringExtra("cur")).findFirst()!!
        curList = realm.where(T_List::class.java).equalTo("id", listID).findFirst()!!.currencys
    }


    fun initLayout(){
        setSupportActionBar(am_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        for(i in 0 until curList.size){
            if(curList[i]!!.code == selectedCurrency.code){
                cSpinner.setSelection(i)
            }
        }
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
                Log.d("mytag", "itemselected " + position.toString())
                if(priceTxt.text.toString() != ""){
                    exchange = selectedCurrency.rate * priceTxt.text.toString().toDouble()
                    inputExchangeTextView(exchange)
                }
            }
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

        priceTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    fun addImg(view: View) {
        //어떤 앱에서 이미지를 가져오는지 몰라서 묵시적 intent 수행
        //가져올 때 액션 picK이라는 인텐트 필터 적용
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, SELECT_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                img_url = getPathFromUri(data!!.data)
                addMoneyImage.setImageURI(data!!.data)
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
        realm.beginTransaction()

        val moneyTable = realm.createObject(T_Money::class.java)//데이터베이스에 저장할 객체 생성

        moneyTable.listID = listID
        moneyTable.dayNum = dayNum
        moneyTable.id = UUID.randomUUID().toString();
        moneyTable.currency = selectedCurrency
        if (img_url == "") { // 사진 선택 안했으면
            moneyTable.img = ""
        } else {
            moneyTable.img = img_url
        }
        moneyTable.price = exchange //한화로 계산
        moneyTable.cate = cate
        moneyTable.date = SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(Date())

        val dayItem = realm.where(T_Day::class.java).equalTo("listID", listID).equalTo("num", dayNum).findFirst()!!
        dayItem.moneyList.add(moneyTable)
        realm.commitTransaction()

        val s = Intent()
        s.putExtra("pos", pos)
        s.putExtra("price", moneyTable.price)
        setResult(Activity.RESULT_OK, s)
        finish()
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
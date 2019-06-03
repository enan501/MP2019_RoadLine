package konkukSW.MP2019.roadline.UI.money

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.Data.DB.T_Money
import kotlinx.android.synthetic.main.activity_add_money.*
import java.util.*
import kotlin.collections.ArrayList



class AddMoneyActivity : AppCompatActivity() {
    var currencyList: ArrayList<Currency> = ArrayList()
    val SELECT_IMAGE = 100
    var img_url: String = "" // 이미지 URI
    var price = 0 // 가격
    var cate: String = "" // 카테고리
    var currencyitem = ""
    // 날짜를 담는 변수 -> intent로 받아와야함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_add_money)
        initPermission()
        init()
    }

    fun initPermission() {
        if (!checkAppPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            askPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), SELECT_IMAGE)
        } else {
            Toast.makeText(
                getApplicationContext(),
                "권한이 승인되었습니다.", Toast.LENGTH_SHORT
            ).show()
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
    } // checkAppPermission

    fun askPermission(requestPermission: Array<String>, REQ_PERMISSION: Int) {
        ActivityCompat.requestPermissions(
            this, requestPermission,
            REQ_PERMISSION
        )
    } // askPermission

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
    } // onRequestPermissionsResult


    fun init() {
        currencyitem = currency.text.toString() // 초기에 화면에 보여진 값
        category_click()
        currencySpinner.onItemSelectedListener = SpinnerSelectedListener()
        money_calculation()
    }

    inner class SpinnerSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            //Toast.makeText(parent?.context, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
            val result = parent?.getItemAtPosition(position).toString()
            currency.text = result
            currencyitem = currency.text.toString()
            // ?. -> null 이 아닐때 수행시키는 연산자
        }
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
                println("data!!.dataString : "+data!!.dataString)
                println("img_url :" + img_url)
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

    fun initCurrency() { // 고를 수 있는 화폐만 출력
        //if()
    }

    //currencyList'
    fun money_calculation() { // 현재 환율 불러와서 원 단위로 환산
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val DBlist = realm.where(T_Currency::class.java).findAll()
        for(T_currency in DBlist) {
            //if(currencyitem)
            println(T_currency.name + " : " + T_currency.code + " : " + T_currency.symbol + " : " + T_currency.rate)
        }

    }

    fun category_click() {
        categoryGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                when (checkedId) {
                    konkukSW.MP2019.roadline.R.id.mealBtn -> {
                        cate = "식사"
                    }
                    konkukSW.MP2019.roadline.R.id.shoppingBtn -> {
                        cate = "쇼핑"
                    }
                    konkukSW.MP2019.roadline.R.id.transportBtn -> {
                        cate = "교통"
                    }
                    konkukSW.MP2019.roadline.R.id.tourBtn -> {
                        cate = "관광"
                    }
                    konkukSW.MP2019.roadline.R.id.lodgmentBtn -> {
                        cate = "숙박"
                    }
                    konkukSW.MP2019.roadline.R.id.etcBtn -> {
                        cate = "기타"
                    }
                }
            }
        }
    }

    fun submitBtn(view: View) {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val moneyTable: T_Money = realm.createObject(T_Money::class.java)//데이터베이스에 저장할 객체 생성
        val moneyTableTuple = realm.where(T_Money::class.java).findAll()

        val i = intent;
        val dayNum = i.getIntExtra("DayNum", 0);
        val listID = i.getStringExtra("ListID");

        moneyTable.listID = listID
        moneyTable.dayNum = dayNum
        moneyTable.id = UUID.randomUUID().toString();
        moneyTable.priceType = ""
        if (img_url == "") { // 사진 선택 안했으면
            moneyTable.img = ""
        } else {
            moneyTable.img = img_url
        }
        moneyTable.price = priceTxt.text.toString().toInt()
        moneyTable.cate = cate
        moneyTable.date = "2018.05.30"
        realm.commitTransaction()

//        val q3 = realm.where(T_Money::class.java).findAll()
//        for (i in 0..q3.size - 1) {
//            System.out.println(q3.get(i)!!.img.toString() + ", " + q3.get(i)!!.price.toString() + ", " + q3.get(i)!!.cate.toString())
//        }
        // 인텐트 넘겨줌
        val s = Intent()
        setResult(Activity.RESULT_OK, s)
        finish()
    }

    fun cancelBtn(view: View) {
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
}
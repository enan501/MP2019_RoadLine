package konkukSW.MP2019.roadline.UI.money

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_add_money.*

class AddMoneyActivity : AppCompatActivity() {
    var ListNumber = 0 // 디비에서 받아올것
    var dayCount = 2 // 디비에서 받아올것
    var planNum = 1 // 디비에서 받아올것

    val SELECT_IMAGE = 100

    lateinit var img_url: String // 이미지 URI
    var price = 0 // 가격
    var cate: String = "" // 카테고리
    // 날짜를 담는 변수 -> intent로 받아와야함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money)
        initPermission()
        init()
    }

    fun initPermission() {
        if (!checkAppPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            askPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), SELECT_IMAGE)
//            val builder = AlertDialog.Builder(this)
//            builder.setMessage("반드시 이미지 데이터에 대한 권한이 허용되어야 합니다.")
//                .setTitle("권한 허용")
//                .setIcon(R.drawable.abc_ic_star_black_48dp)
//            builder.setPositiveButton("OK") { _, _ ->
//            }
//            val dialog = builder.create()
//            dialog.show()
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

        category_click()
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
                img_url = data!!.dataString
                addMoneyImage.setImageURI(data!!.data)
            }
        }
    }

    fun money_calculation() { // 현재 환율 불러와서 원 단위로 환산

    }

    fun category_click() {
        categoryGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                when (checkedId) {
                    R.id.mealBtn -> {
                        cate = "식사"
                    }
                    R.id.shoppingBtn -> {
                        cate = "쇼핑"
                    }
                    R.id.transfortBtn -> {
                        cate = "교통"
                    }
                    R.id.tourBtn -> {
                        cate = "관광"
                    }
                    R.id.lodgmentBtn -> {
                        cate = "숙박"
                    }
                    R.id.etcBtn -> {
                        cate = "etc"
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

        moneyTable.listNum = ListNumber
        moneyTable.dayNum = dayCount
        moneyTable.planNum = planNum
        moneyTable.num = moneyTableTuple.size
        moneyTable.priceType = ""
        moneyTable.img = img_url
        moneyTable.price = priceTxt.text.toString().toInt()
        moneyTable.cate = cate
        moneyTable.date = "2018.05.30"
        realm.commitTransaction()

//        val q3 = realm.where(T_Money::class.java).findAll()
//        for (i in 0..q3.size - 1) {
//            System.out.println(q3.get(i)!!.img.toString() + ", " + q3.get(i)!!.price.toString() + ", " + q3.get(i)!!.cate.toString())
//        }

        // 인텐트 넘겨줌
        val intent = Intent(this, ShowMoneyActivity::class.java)
        startActivity(intent)
    }

    fun cancelBtn(view: View) {
        val intent = Intent(this, ShowMoneyActivity::class.java)
        startActivity(intent)
    }
}
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
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_add_money.*

class AddMoneyActivity : AppCompatActivity() {
    val SELECT_IMAGE = 100

    lateinit var img_url: String // 이미지 URI
    var price = 0 // 가격
    var categor_num = -1 // 카테고리
    // 날짜를 담는 변수 -> intent로 받아와야함

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money)
        initPermission()
        init()
    }

    fun initPermission() {
        if (!checkAppPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("반드시 이미지 데이터에 대한 권한이 허용되어야 합니다.")
                .setTitle("권한 허용")
                .setIcon(R.drawable.abc_ic_star_black_48dp)
            builder.setPositiveButton("OK") { _, _ ->
                askPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), SELECT_IMAGE)
            }
            val dialog = builder.create()
            dialog.show()
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
                        categor_num = 0
                    }
                    R.id.shoppingBtn -> {
                        categor_num = 1
                    }
                    R.id.transfortBtn -> {
                        categor_num = 2
                    }
                    R.id.tourBtn -> {
                        categor_num = 3
                    }
                    R.id.lodgmentBtn -> {
                        categor_num = 4
                    }
                    R.id.etcBtn -> {
                        categor_num = 5
                    }
                }
            }
        }
    }

    fun submitBtn(view: View) {

        price = priceTxt.text.toString().toInt()
    }
}
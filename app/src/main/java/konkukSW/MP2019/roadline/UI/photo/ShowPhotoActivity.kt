package konkukSW.MP2019.roadline.UI.photo

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.MoneyListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.DB.T_Photo
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_show_photo.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class ShowPhotoActivity : AppCompatActivity() {

    lateinit var rViewAdapter: MoneyListAdapter
    var realm = Realm.getDefaultInstance()
    var ListID = ""
    var DayNum = 0
    var isAll = false
    lateinit var dayList: RealmResults<T_Day>

    private val SELECT_IMAGE = 100
    private var day_click = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_photo)
        init()
    }

    fun init(){
        Realm.init(this)
        initPermission()
        initData()
        initLayout()
        initListener()
    }

    fun initData(){
        val i = intent
        ListID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum", 0)

        if(DayNum == 0){ //모든 날짜
            dayList = realm.where(T_Day::class.java).equalTo("listID", ListID).findAll()!!.sort("num")
            isAll = true
        }
        else{
            dayList =  realm.where(T_Day::class.java).equalTo("listID", ListID).equalTo("num", DayNum).findAll()!!
            isAll = false
        }
        rViewAdapter = MoneyListAdapter(dayList, this@ShowPhotoActivity, isAll, false)
    }

    fun initPermission() {
        if (!checkAppPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            askPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), SELECT_IMAGE)
        } else {
            Toast.makeText(
                applicationContext,
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                realm.beginTransaction()
                val Table: T_Photo = realm.createObject(T_Photo::class.java, UUID.randomUUID().toString())//데이터베이스에 저장할 객체 생성
                Table.listID = ListID
                Table.dayNum = day_click
                Table.img = getPathFromUri(data!!.data)
                Table.date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                realm.commitTransaction()
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

    fun initListener() {
        rViewAdapter.photoItemClickListener = object :MoneyListAdapter.OnPhotoItemClickListener{
            override fun onButtonClick(holder: MoneyListAdapter.ViewHolder, view: View, data: T_Day, position: Int) {
                if(isAll){
                    day_click = position + 1
                }
                else{
                    day_click = DayNum
                }
                addImg()
            }

            override fun onPhotoItemClick(data: T_Photo) {
                showImage(data)
            }
        }
    }

    fun addImg() {
        //어떤 앱에서 이미지를 가져오는지 몰라서 묵시적 intent 수행
        //가져올 때 액션 picK이라는 인텐트 필터 적용
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
        intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, SELECT_IMAGE)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initLayout() {
        setSupportActionBar(sp_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        photo_recycleView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        photo_recycleView.adapter = rViewAdapter
        val animator = photo_recycleView.itemAnimator
        if(animator is SimpleItemAnimator){
            animator.supportsChangeAnimations = false
        }
    }


    fun showImage(item: T_Photo) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //레이아웃을 위에 겹쳐서 올리는 부분
        val layout = inflater.inflate(R.layout.detail_photo_img, null) as ConstraintLayout //레이아웃 객체생성
        layout.setBackgroundColor(Color.parseColor("#99000000")) //레이아웃 배경 투명도 주기
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        addContentView(layout, lp) //레이아웃 위에 겹치기
        layout.setOnClickListener {
            (layout.parent as ViewManager).removeView(layout)
        }
        var imageView = layout.findViewById<ImageView>(R.id.priceImage) // 매번 새로운 레이어 이므로 ID를 find 해준다.
        var textView2 = layout.findViewById<TextView>(R.id.textView2)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        textView2.text = dateFormat.format(item!!.date)
        if (item.img == "")
            imageView.setImageResource(R.drawable.logo)
        else
            imageView.setImageBitmap(BitmapFactory.decodeFile(item.img))

        var button = layout.findViewById<Button>(R.id.selectMainImg)
        button.setOnClickListener {
            // 대표사진으로 설정하는 코드
            realm.beginTransaction()
            var list = realm.where(T_List::class.java).equalTo("id",item.listID).findFirst()
            list!!.img = item.img
            realm.commitTransaction()
            val builder = AlertDialog.Builder(this)
            builder.setMessage("대표사진으로 설정되었습니다.")
            builder.setPositiveButton("확인") { _, _ ->
            }.show()
        }
    }
}



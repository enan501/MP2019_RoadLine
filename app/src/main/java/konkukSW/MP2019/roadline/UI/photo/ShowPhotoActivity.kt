package konkukSW.MP2019.roadline.UI.photo

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.MoneyPhotoListAdapter
import konkukSW.MP2019.roadline.Data.Adapter.PhotoGridAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Photo
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_show_money.*
import kotlinx.android.synthetic.main.activity_show_photo.*
import kotlinx.android.synthetic.main.activity_show_photo.deleteButton
import kotlinx.android.synthetic.main.activity_show_photo.deleteText
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class ShowPhotoActivity : AppCompatActivity() {

    lateinit var rViewAdapterPhoto: MoneyPhotoListAdapter
    lateinit var realm :Realm
    var ListID = ""
    var DayNum = 0
    var isAll = false
    lateinit var dayList: RealmResults<T_Day>
    var img_path = ""//카메라 이미지 경로
    private val SELECT_IMAGE = 100
    private val CAPTURE_IMAGE = 200
    private var day_click = 0
    var deleteMode = false
    var deletePhotoList: ArrayList<T_Photo> = ArrayList()


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
        realm = Realm.getDefaultInstance()
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
        rViewAdapterPhoto = MoneyPhotoListAdapter(dayList, this@ShowPhotoActivity, isAll, false)
    }

    fun initLayout() {
        setSupportActionBar(sp_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "추억함"

        photo_recycleView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        photo_recycleView.adapter = rViewAdapterPhoto
        val animator = photo_recycleView.itemAnimator
        if(animator is SimpleItemAnimator){
            animator.supportsChangeAnimations = false
        }
    }

    fun initListener() {
        rViewAdapterPhoto.photoItemClickListener = object :MoneyPhotoListAdapter.OnPhotoItemClickListener{
            override fun onButtonClick(holder: MoneyPhotoListAdapter.ViewHolder, view: View, data: T_Day, position: Int) {
                if(isAll){
                    day_click = position + 1
                }
                else{
                    day_click = DayNum
                }
                val array = arrayOf("사진 찍어서 추가", "앨범에서 추가")
                val dialog = AlertDialog.Builder(this@ShowPhotoActivity)
                dialog.setItems(array, DialogInterface.OnClickListener { dialog, which ->
                    when(which){
                        0->{ //카메라
                            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                            if(intent.resolveActivity(packageManager) != null){
                                var photoFile: File? = null
                                try {
                                    photoFile = createImageFile()
                                }catch (e: IOException){}
                                if(photoFile != null){
                                    val photoUri = FileProvider.getUriForFile(this@ShowPhotoActivity, packageName + ".fileprovider", photoFile)
                                    img_path = photoFile.absolutePath
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                                    startActivityForResult(intent, CAPTURE_IMAGE)
                                }
                            }
                        }
                        1->{ //앨범
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE
                            intent.data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            startActivityForResult(intent, SELECT_IMAGE)
                        }
                    }
                })
                dialog.show()
            }

            override fun onPhotoItemClick(
                    holder: PhotoGridAdapter.ViewHolder,
                    view: View,
                    data: T_Photo,
                    position: Int,
                    isChecked: Boolean
            ) {
                if(deleteMode){
                    if(isChecked){
                        holder.checkButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                        deletePhotoList.remove(data)
                    }
                    else{
                        holder.checkButton.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
                        deletePhotoList.add(data)
                    }
                }
            }
        }

        deleteButton.setOnClickListener {
            if(deleteMode){
                if(deletePhotoList.isNotEmpty()){
                    val builder = androidx.appcompat.app.AlertDialog.Builder(this@ShowPhotoActivity)
                    builder.setMessage("삭제하시겠습니까?")
                            .setPositiveButton("삭제") { dialogInterface, _ ->
                                Log.d("mytag", deletePhotoList.toString())
                                realm.beginTransaction()
                                for(i in deletePhotoList){
                                    i.deleteFromRealm()
                                }
                                realm.commitTransaction()
                                changeViewToDeleteMode(deleteMode)
                                deletePhotoList.clear()
                                deleteText.text = "수정하기"
                                deleteMode = false
                            }
                            .setNegativeButton("취소") { dialogInterface, i ->
                                changeViewToDeleteMode(deleteMode)
                                deletePhotoList.clear()
                                deleteText.text = "수정하기"
                                deleteMode = false
                            }
                    val dialog = builder.create()
                    dialog.show()
                }
                else{
                    changeViewToDeleteMode(deleteMode)
                    deleteText.text = "수정하기"
                    deleteMode = false
                }
            }
            else{
                deleteText.text = "삭제하기"
                changeViewToDeleteMode(deleteMode)
                deleteMode = true
            }
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "TEST_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }

    fun changeViewToDeleteMode(flg: Boolean){
        for(i in 0 until rViewAdapterPhoto.itemCount){
            val viewHolder = photo_recycleView.findViewHolderForAdapterPosition(i)
            if(viewHolder != null){
                for(j in 0 until (viewHolder as MoneyPhotoListAdapter.ViewHolder).rView.childCount){
                    val itemViewHolder = viewHolder.rView.findViewHolderForAdapterPosition(j)
                    if(itemViewHolder != null){
                        if(flg){
                            (itemViewHolder as PhotoGridAdapter.ViewHolder).isChecked = false
                            itemViewHolder.checkButton.visibility = View.INVISIBLE
                            itemViewHolder.imgCover.visibility = View.INVISIBLE
                        }
                        else{
                            (itemViewHolder as PhotoGridAdapter.ViewHolder).isChecked = false
                            itemViewHolder.checkButton.visibility = View.VISIBLE
                            itemViewHolder.imgCover.visibility = View.VISIBLE
                            itemViewHolder.checkButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                        }

                    }
                }
            }
        }
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
    } // onRequestPermissionsResult

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var path = ""
            when(requestCode){
                SELECT_IMAGE->{
                    path = getPathFromUri(data!!.data)
                }
                CAPTURE_IMAGE->{
                    path = img_path
                }
            }
            realm.beginTransaction()
            val Table: T_Photo = realm.createObject(T_Photo::class.java, UUID.randomUUID().toString())//데이터베이스에 저장할 객체 생성
            Table.listID = ListID
            Table.dayNum = day_click
            Table.img = path
            if(android.os.Build.VERSION.SDK_INT >= 26) {
                Table.dateTime = LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond()
            }
            else{
                Table.dateTime = org.threeten.bp.LocalDateTime.now().atZone(org.threeten.bp.ZoneId.of("Asia/Seoul")).toEpochSecond()
            }
            realm.commitTransaction()
        }
    }

    fun getPathFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToNext()
        val path = cursor.getString(cursor.getColumnIndex("_data"))
        cursor.close()
        return path
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}



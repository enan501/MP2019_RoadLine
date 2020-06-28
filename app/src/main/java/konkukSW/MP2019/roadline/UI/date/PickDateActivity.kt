package konkukSW.MP2019.roadline.UI.date

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import konkukSW.MP2019.roadline.Data.Adapter.PhotoPickGridAdapter
import konkukSW.MP2019.roadline.Data.Adapter.PickDateAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.DB.T_Photo
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.PickDate
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import kotlinx.android.synthetic.main.activity_pick_date.*
import kotlinx.android.synthetic.main.image_pick_dialog.*
import kotlinx.android.synthetic.main.item_pick_date.*
import kotlin.collections.ArrayList


class PickDateActivity : AppCompatActivity() {
    var ListID = ""
    var listPos = -1

    var snapHelper = LinearSnapHelper()
    lateinit var dateList:ArrayList<PickDate>
    lateinit var PDAdapter: PickDateAdapter
    lateinit var realm: Realm
    lateinit var thisList:T_List
    lateinit var backgroundImg:String
    lateinit var dayResults: RealmResults<T_Day>
    var editMode = false
    var pickedDay = -1
    val layoutManager by lazy{
        CenterZoomLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_date)
        init()
    }
    fun init(){
        Realm.init(this)
        initData()
        initLayout()
        addListener()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            setResult(listPos)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initData(){
        ListID = intent.getStringExtra("ListID")
        listPos = intent.getIntExtra("listPos", -1)
        backgroundImg = intent.getStringExtra("backgroundImg")
        dateList = arrayListOf()

        realm = Realm.getDefaultInstance()
        thisList = realm.where<T_List>(T_List::class.java).equalTo("id",ListID).findFirst()!!
        dayResults = realm.where<T_Day>(T_Day::class.java)
                .equalTo("listID", ListID)
                .findAll().sort("num")
        for(T_Day in dayResults){
            dateList.add(PickDate(ListID, T_Day.num, T_Day.date, T_Day.img))
        }

    }

    fun initLayout(){
        Glide.with(this).load(backgroundImg).centerCrop().into(ivBackgroundImg)
        setSupportActionBar(PD_toolbar)
        val backArrow = ContextCompat.getDrawable(applicationContext, R.drawable.abc_ic_ab_back_material)
        backArrow!!.setColorFilter(ContextCompat.getColor(applicationContext, R.color.white), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        PD_title.text = thisList.title

        val smoothScroller = object : androidx.recyclerview.widget.LinearSmoothScroller(this) {
            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_END
            }
        }
        PD_rView.layoutManager = layoutManager
        var half = resources.displayMetrics.widthPixels/3
        PD_rView.setPadding(half,0,half,0)
        snapHelper.attachToRecyclerView(PD_rView) //아이템 가운데로 끌어 맞추기
        PDAdapter = PickDateAdapter(applicationContext, dateList)
        PD_rView.adapter = PDAdapter
        smoothScroller.targetPosition = 0
        smoothScroller.computeScrollVectorForPosition(0)
        layoutManager.startSmoothScroll(smoothScroller)
        PDAdapter.notifyDataSetChanged()
    }


    fun addListener() {
        PDAdapter.itemClickListener = object : PickDateAdapter.OnItemClickListener {
            override fun OnItemClick(holder: PickDateAdapter.ViewHolder, data: PickDate, position: Int) {
                if(!editMode){
                        var PDIntentToSD = Intent(applicationContext, ShowDateActivity::class.java)
                        PDIntentToSD.putExtra("ListID", ListID)
                        PDIntentToSD.putExtra("DayNum", data.day)
                        startActivity(PDIntentToSD)
                }
                else{
                    editMode = false
                    addImageButton.visibility = View.INVISIBLE
                }
//                else{
//                    editMode = false
//                    addImageButton.visibility = View.INVISIBLE
//                }
            }

            override fun OnItemLongClick(holder: PickDateAdapter.ViewHolder, data: PickDate, position: Int) {
                editMode = true
                addImageButton.visibility = View.VISIBLE
                pickedDay = data.day
            }

        }

//        PD_rView.setOnClickListener{
//            Log.d("mytag", "clickkk")
//            editMode = false
//            addImageButton.visibility = View.INVISIBLE
//        }


        addImageButton.setOnClickListener {
            var position = layoutManager.getPosition(snapHelper.findSnapView(layoutManager)!!)
            Log.d("enan","position : $position")
            showImagePickDialog(position+1)
        }

        PD_photoBtn.setOnClickListener {
            var intent = Intent(this, ShowPhotoActivity::class.java)
            intent.putExtra("ListID", ListID)
            intent.putExtra("DayNum",0) // 0:모든 Day 사진첩 전체 출력/ 1이상이면 그것만 출력
            startActivity(intent)

            overridePendingTransition(
                R.anim.anim_slide_in_top,
                R.anim.anim_slide_out_bottom
            )
        }
        PD_moneyBtn.setOnClickListener {
            var PDIntentToMoney = Intent(this, ShowMoneyActivity::class.java)
            PDIntentToMoney.putExtra("ListID", ListID)
            PDIntentToMoney.putExtra("DayNum",0) // 0:모든 Day 가계부 전체 출력/ 1이상이면 그것만 출력
            startActivity(PDIntentToMoney)
            overridePendingTransition(
                R.anim.anim_slide_in_top,
                R.anim.anim_slide_out_bottom
            )
        }
    }

    fun showImagePickDialog(dayNum: Int){
        var photoResults = realm.where(T_Photo::class.java).equalTo("listID", ListID).equalTo("dayNum", dayNum).findAll().sort("dayNum", Sort.ASCENDING, "dateTime", Sort.ASCENDING)
        var clickedPhoto:T_Photo? = null
        val imagePickBuilder = AlertDialog.Builder(this@PickDateActivity)
        val imagePickView = layoutInflater.inflate(R.layout.image_pick_dialog, null)
        val dayTextView = imagePickView.findViewById<TextView>(R.id.dayText)
        val pickImageView = imagePickView.findViewById<RecyclerView>(R.id.rView)
        val backTextView = imagePickView.findViewById<TextView>(R.id.backTextView)
        var photoAdapter: PhotoPickGridAdapter?
        var isAvail = false
        pickImageView.setHasFixedSize(true)

        dayTextView.visibility = View.VISIBLE
        dayTextView.text = "Day " + dayNum.toString()
        if(photoResults.size == 0){
            backTextView.visibility = View.VISIBLE
            pickImageView.visibility = View.INVISIBLE
        }
        else{
            isAvail = true
            backTextView.visibility = View.INVISIBLE
            pickImageView.visibility = View.VISIBLE
            photoAdapter = PhotoPickGridAdapter(photoResults, this@PickDateActivity)
            pickImageView.adapter = photoAdapter
            pickImageView.layoutManager = GridLayoutManager(this@PickDateActivity, 3)

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
                    val builder = AlertDialog.Builder(this@PickDateActivity)
                    builder.setMessage("사진을 선택해주세요")
                            .setPositiveButton("확인") { _, _ ->

                            }
                    val dialog = builder.create()
                    dialog.show()
                }
                else{
                    createdBuilder.dismiss()
                    val dayItem = dayResults.where().equalTo("num", dayNum).findFirst()
                    realm.beginTransaction()
                    dayItem!!.img = clickedPhoto!!.img
                    realm.commitTransaction()
                    dateList[dayNum - 1].img = clickedPhoto!!.img
                    PDAdapter.notifyItemChanged(dayNum - 1)
                }
            }
        }
    }

}

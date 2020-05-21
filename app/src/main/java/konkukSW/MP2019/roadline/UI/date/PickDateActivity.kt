package konkukSW.MP2019.roadline.UI.date

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.LinearSnapHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.PickDateAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.Dataclass.PickDate
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import kotlinx.android.synthetic.main.activity_pick_date.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class PickDateActivity : AppCompatActivity() {
    var ListID = ""

    var snapHelper = LinearSnapHelper()
    lateinit var dateList:ArrayList<PickDate>
    lateinit var PDAdapter: PickDateAdapter
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_date)
        init()

    }
    fun init(){
        initData()
        initLayout()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initLayout(){
        setSupportActionBar(PD_toolbar)
        val backArrow = ContextCompat.getDrawable(applicationContext, R.drawable.abc_ic_ab_back_material)
        backArrow!!.setColorFilter(ContextCompat.getColor(applicationContext, R.color.white), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        val layoutManager = CenterZoomLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val smoothScroller = object : LinearSmoothScroller(this) {
            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_END
            }
        }
        PD_rView.layoutManager = layoutManager
        snapHelper.attachToRecyclerView(PD_rView) //아이템 가운데로 끌어 맞추기
        PDAdapter = PickDateAdapter(dateList)
        PD_rView.adapter = PDAdapter
        smoothScroller.targetPosition = 2
        smoothScroller.computeScrollVectorForPosition(2)
        layoutManager.startSmoothScroll(smoothScroller)
        dateList.removeAt(0)
        PDAdapter.notifyDataSetChanged()
        addListener()
    }

    fun initData(){
        ListID = intent.getStringExtra("ListID")
        dateList = arrayListOf(PickDate(ListID,0,"blank"),PickDate(ListID,0,"first"))

        Realm.init(this)
        realm = Realm.getDefaultInstance()

        PD_title.setText(realm.where<T_List>(T_List::class.java).equalTo("id",ListID).findFirst()!!.title)
        val results = realm.where<T_Day>(T_Day::class.java)
            .equalTo("listID",ListID)
            .findAll()
        for(T_Day in results){
            dateList.add(PickDate(ListID,T_Day.num,T_Day.date))
        }
        dateList.add(PickDate(ListID,-1,"ADD"))
        dateList.add(PickDate(ListID,0,"last"))
    }
    fun addListener() {
        PDAdapter.itemClickListener = object : PickDateAdapter.OnItemClickListener {
            override fun OnItemClick(holder: PickDateAdapter.ViewHolder, data: PickDate, position: Int) {
                if(data.day > 0){
                var PDIntentToSD = Intent(applicationContext, ShowDateActivity::class.java)
                PDIntentToSD.putExtra("ListID", ListID)
                PDIntentToSD.putExtra("DayNum", data.day)
                startActivity(PDIntentToSD)
                }
                else if(data.day == -1){ //추가
                    //db에다 여행 날짜 추가
                    realm.beginTransaction()
                    val newDay: T_Day = realm.createObject(T_Day::class.java)
                    newDay.listID = data.listid
                    newDay.num = dateList[position-1].day + 1
                    newDay.date = LocalDate.parse(dateList[position-1].date, DateTimeFormatter.ofPattern("yyyy. M. d")).plusDays(1)
                        .format( DateTimeFormatter.ofPattern("yyyy. M. d"))
                    realm.commitTransaction()

                    dateList.add(position,PickDate(ListID,newDay.num,newDay.date))
                    PDAdapter.notifyDataSetChanged()
                }
            }
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
/*    fun notifySnapPositionChanged(rView:RecyclerView){
        val snapView = snapHelper.findSnapView(PD_rView.layoutManager)
        val snapPosition = PD_rView.layoutManager!!.getPosition(snapView!!)
    }*/
}

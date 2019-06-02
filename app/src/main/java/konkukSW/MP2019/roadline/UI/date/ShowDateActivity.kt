package konkukSW.MP2019.roadline.UI.date

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.TabAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import kotlinx.android.synthetic.main.activity_show_date.*
import android.support.v4.view.ViewPager


class ShowDateActivity : AppCompatActivity() {

    var ListID:String = ""
    var DayNum:Int = 0
    var title:String = ""
    var date:String = ""
    var maxDay:Int = 0

    private var tabLayer:TabLayout?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_show_date)
        init()
    }

    fun init(){
        initData()
        pullDB()
        initLayout()
        initListener()
    }

    fun pullDB(){
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val listResult: T_List = realm.where(T_List::class.java).equalTo("id", ListID).findFirst()!!
        title = listResult.title
        val dayResult: T_Day = realm.where(T_Day::class.java).equalTo("listID", ListID).equalTo("num", DayNum).findFirst()!!
        date = dayResult.date
        maxDay = realm.where(T_Day::class.java).equalTo("listID", ListID).findAll().size
    }

    fun initData(){
        tabLayer = findViewById(konkukSW.MP2019.roadline.R.id.sd_layout_tab)
        tabLayer!!.addTab(tabLayer!!.newTab().setText("리스트"))
        tabLayer!!.addTab(tabLayer!!.newTab().setText("가로 타임라인"))
        tabLayer!!.addTab(tabLayer!!.newTab().setText("세로 타임라인"))
        tabLayer!!.addTab(tabLayer!!.newTab().setText("지도"))

        ListID = intent.getStringExtra("ListID")
        DayNum = intent.getIntExtra("DayNum", 0)

    }

    fun initLayout(){
        sd_textView_name.setText(title)
        sd_textView_date.setText(date)
        sd_textView_day.setText("DAY" + DayNum.toString())
    }

    fun initListener(){
        val adapter = TabAdapter(supportFragmentManager, tabLayer!!.tabCount, ListID, DayNum)
        sd_viewPager.adapter = adapter
        sd_viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayer))
        tabLayer!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                sd_viewPager.currentItem = tab.position
                if(tab.position == 1) {
                    (getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(tab.position))
                            as Fragment2).init()
                }
            }
        })

        sd_leftImg.setOnClickListener {
            if(DayNum>=2){
                val i = Intent(this, ShowDateActivity::class.java)
                i.putExtra("DayNum", DayNum-1)
                i.putExtra("ListID", ListID)
                startActivity(i)
                overridePendingTransition(konkukSW.MP2019.roadline.R.anim.anim_slide_in_left, konkukSW.MP2019.roadline.R.anim.anim_slide_out_right)
                finish()
            }
        }

        sd_rightImg.setOnClickListener {
            if(DayNum < maxDay){
                val i = Intent(this, ShowDateActivity::class.java)
                i.putExtra("DayNum", DayNum+1)
                i.putExtra("ListID", ListID)
                startActivity(i)
                overridePendingTransition(konkukSW.MP2019.roadline.R.anim.anim_slide_in_right, konkukSW.MP2019.roadline.R.anim.anim_slide_out_left)
                finish()
            }
        }

        sd_imgBtn1.setOnClickListener {
            //사진첩 버튼
            startActivity(Intent(this, ShowPhotoActivity::class.java))
        }

        sd_imgBtn2.setOnClickListener {
            //가계부 버튼
            var PDIntentToMoney = Intent(this, ShowMoneyActivity::class.java)
            PDIntentToMoney.putExtra("ListID", ListID)
            PDIntentToMoney.putExtra("DayNum", DayNum) // 0:모든 Day 가계부 전체 출력/ 1이상이면 그것만 출력
            startActivity(PDIntentToMoney)
        }

        sd_imgBtn3.setOnClickListener {
            //공유 버튼
        }

    }

}

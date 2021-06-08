package konkukSW.MP2019.roadline.UI.date


import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.tabs.TabLayoutMediator
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import konkukSW.MP2019.roadline.Data.Adapter.DayListAdapter
import konkukSW.MP2019.roadline.Data.Adapter.TabAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.PickDate
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import kotlinx.android.synthetic.main.activity_show_date.*
import kotlinx.android.synthetic.main.fragment_fragment2.*
import kotlinx.android.synthetic.main.item_date_button.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors


class ShowDateActivity : AppCompatActivity() {
    private val TYPE_BEFORE = 0
    private val TYPE_NEXT = 1

    private val planFragments = arrayListOf<Fragment>(
            Fragment1(),
            Fragment2(),
            Fragment4()
    )
    val days by lazy{
        dayResults.stream().map{PickDate(listID, it.num, it.date, it.img)}.collect(Collectors.toList())
    }
    var listID = "a"
    var dayNum:Int? = 0
    var maxDayNum = 0
    lateinit var adapter: TabAdapter
    lateinit var realm: Realm
    private var tabLayer: TabLayout? = null
    var tabPos: Int = 0
    var down_x = 0f
    var up_x = 0f
    var title = ""
    lateinit var thisList: T_List
    lateinit var dayResults: RealmResults<T_Day>
    lateinit var planResults:RealmResults<T_Plan>
    val plans = MutableLiveData<T_Plan>()


    val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() // ◀ StartActivityForResult 처리를 담당
    ) { activityResult ->
        // action to do something
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_date)
        init()
    }

    fun init() {
        initData()
        initLayout()
        initListener()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                finish()
            }
            R.id.menuPhoto -> {
                var Intent = Intent(this, ShowPhotoActivity::class.java)
                Intent.putExtra("ListID", listID)
                Intent.putExtra("DayNum", dayNum) // 0:모든 Day 사진첩 전체 출력/ 1이상이면 그것만 출력
                startActivity(Intent)
                overridePendingTransition(
                        R.anim.anim_slide_in_top,
                        R.anim.anim_slide_out_bottom
                )
            }
            R.id.menuMoney -> {
                //가계부 버튼
                var PDIntentToMoney = Intent(this, ShowMoneyActivity::class.java)
                PDIntentToMoney.putExtra("ListID", listID)
                PDIntentToMoney.putExtra("DayNum", dayNum) // 0:모든 Day 가계부 전체 출력/ 1이상이면 그것만 출력
                startActivity(PDIntentToMoney)
                overridePendingTransition(
                        R.anim.anim_slide_in_top,
                        R.anim.anim_slide_out_bottom
                )
            }
//            R.id.menuShare -> {
//                var bitmap: Bitmap? = null
//                if (tabPos == 0) { //Fragment1
//                    bitmap = (supportFragmentManager
//                            .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(0))
//                            as Fragment1).getScreenshot()
//
//                }
//                else if(tabPos == 1){ //Fragment2
//                    bitmap = (supportFragmentManager
//                            .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(1))
//                            as Fragment2).getScreenshotFromRecyclerView()
//                }
//                else if(tabPos == 2){
//                    (supportFragmentManager
//                            .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(2))
//                            as Fragment4).getScreenShot()
//                }
//
//                if(bitmap != null){
//                    val storage = this.cacheDir
//                    val fileName = "temp.jpg"
//                    val tempFile = File(storage, fileName)
//                    try {
//                        tempFile.createNewFile()
//                        val out = FileOutputStream(tempFile)
//                        bitmap.compress(Bitmap.CompressFormat.JPEG,100, out)
//                        out.close()
//                    } catch (e: FileNotFoundException) {
//                        e.printStackTrace()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                    Log.v("tag", tempFile.toURI().toString())
//                    var uri = FileProvider.getUriForFile(this,packageName + ".fileprovider",tempFile)
//                    val shareIntent = Intent(Intent.ACTION_SEND)
//                    shareIntent.addCategory(Intent.CATEGORY_DEFAULT)
//                    shareIntent.type = "image/*"
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//                    startActivity(Intent.createChooser(shareIntent, "여행 일정 공유"))
//                }
//            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    fun setPlans(selectedDay:Int?) {
        if (dayNum != null) {
            if(dayNum != selectedDay) {
                days[dayNum!!].isSelected = false
                (rvDates.adapter as DayListAdapter).notifyItemChanged(dayNum!!)
            }

        } else{
            btnAll.tvDateIcon.isSelected = false
        }
        if (selectedDay != null) {
            planResults = realm.where<T_Plan>(T_Plan::class.java)
                    .equalTo("listID", listID)
                    .equalTo("dayNum", selectedDay)
                    .findAll()
                    .sort("pos")
        } else {
            planResults =  realm.where<T_Plan>(T_Plan::class.java)
                    .equalTo("listID", listID)
                    .findAll()
                    .sort("dayNum",Sort.ASCENDING,"pos",Sort.ASCENDING)

        }
        supportFragmentManager.fragments.apply {
            if(this.size >0) {
                (this[0] as Fragment1).init()
                (this[1] as Fragment2).init()
                (this[2] as Fragment4).init()
            }
        }

        dayNum = selectedDay
        Log.d("enan",selectedDay.toString())
    }

    fun initData() {
        listID = intent.getStringExtra("ListID")
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        thisList = realm.where<T_List>(T_List::class.java).equalTo("id", listID).findFirst()!!
        dayResults = realm.where<T_Day>(T_Day::class.java)
                .equalTo("listID", listID)
                .findAll().sort("num")
        rvDates.adapter = DayListAdapter(object:DayListAdapter.OnItemClickListener{
            override fun onItemClick(dayNum: Int?) {
                setPlans(dayNum)
            }
        })
        rvDates.itemAnimator = null
        days[0].isSelected = true
        (rvDates.adapter as DayListAdapter).submitList(days)
        maxDayNum = dayResults.size
        title = thisList.title
        sd_toolbar.title = title
        setPlans(null)
        btnAll.isSelected = true
    }

    fun initLayout(){
        setSupportActionBar(sd_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        btnAll.tvDateIcon.text = "A"
        btnAll.tvDate.text = "ALL"

    }


    fun initListener() {
        btnAll.setOnClickListener {
            setPlans(null)
            btnAll.tvDateIcon.isSelected = true
        }
        sd_viewPager.adapter = PlanViewPagerAdapter(planFragments, this)
        sd_viewPager.offscreenPageLimit = 3
        TabLayoutMediator(sd_layout_tab,sd_viewPager){tab, position ->
            tab.icon = when(position){
                0->{ ContextCompat.getDrawable(this,R.drawable.tab_list)}
                1->{ ContextCompat.getDrawable(this,R.drawable.tab_timeline)}
                else->{ ContextCompat.getDrawable(this,R.drawable.tab_map)}
            }
            tab.view.alpha = 0.4f
        }.attach()
        sd_layout_tab.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.textBlue))
        sd_layout_tab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) { tab?.view?.alpha = 1f }
            override fun onTabUnselected(tab: TabLayout.Tab?) { tab?.view?.alpha = 0.4f }
            override fun onTabReselected(tab: TabLayout.Tab?) { tab?.view?.alpha = 1f }
        })

        sd_addPlanBtn.setOnClickListener {
            val i = Intent(this, AddSpotActivity::class.java)
            i.putExtra("pos", planResults.count() - 1)
            i.putExtra("DayNum", dayNum)
            i.putExtra("ListID", listID)
            startActivity(i)
        }


        // -------------------------
//        sd_day_layout.setOnTouchListener { v, event ->
//            if(event.action == MotionEvent.ACTION_DOWN){
//                down_x = event.rawX
//            }
//            else if(event.action == MotionEvent.ACTION_UP){
//                up_x = event.rawX
//                if(up_x - down_x > 100){
//                    //왼쪽으로 넘어가기
//                    if(dayNum != 1)
//                        goIntent(TYPE_BEFORE)
//                }
//                else if(up_x - down_x < -100){
//                    //오른쪽으로 넘어가기
//                    if(dayNum != maxDayNum)
//                        goIntent(TYPE_NEXT)
//                }
//            }
//            true
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }




}





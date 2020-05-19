package konkukSW.MP2019.roadline.UI.date

//import com.kakao.util.KakaoParameterException
//import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder
//import com.kakao.kakaolink.KakaoLink
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.TabAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import kotlinx.android.synthetic.main.activity_show_date.*
import kotlinx.android.synthetic.main.fragment_fragment2.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class ShowDateActivity : AppCompatActivity() {
    var ListID = "a"
    var DayNum = 0
    var maxDayNum = 0
    lateinit var adapter: TabAdapter
    lateinit var realm: Realm
    private var tabLayer: TabLayout? = null
    var tabPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_date)
        init()
    }

    fun init() {
        initData()
        initListener()
    }

    fun back(){
        finish()
    }

    fun initData() {
        tabLayer = findViewById(konkukSW.MP2019.roadline.R.id.sd_layout_tab)
        tabLayer!!.addTab(tabLayer!!.newTab().setIcon(konkukSW.MP2019.roadline.R.drawable.tab_list_select))
        tabLayer!!.addTab(tabLayer!!.newTab().setIcon(konkukSW.MP2019.roadline.R.drawable.tab_timeline))
        tabLayer!!.addTab(tabLayer!!.newTab().setIcon(konkukSW.MP2019.roadline.R.drawable.tab_map))

        ListID = intent.getStringExtra("ListID")
        DayNum = intent.getIntExtra("DayNum", 0)
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        maxDayNum = realm.where<T_Day>(T_Day::class.java).equalTo("listID", ListID).findAll().size
        title_view.setText(realm.where<T_List>(T_List::class.java).equalTo("id", ListID).findFirst()!!.title.toString())
        sd_textView1.setText("DAY " + DayNum.toString())
        sd_textView2.setText(
                realm.where<T_Day>(T_Day::class.java).equalTo("listID", ListID).equalTo(
                        "num",
                        DayNum
                ).findFirst()!!.date
        )
    }

    fun initListener() {
        adapter = TabAdapter(supportFragmentManager, tabLayer!!.tabCount)
        sd_viewPager.adapter = adapter
        //sd_viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayer))
        sd_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        tabLayer!!.getTabAt(0)?.setIcon(R.drawable.tab_list_select)
                        tabLayer!!.getTabAt(1)?.setIcon(R.drawable.tab_timeline)
                        tabLayer!!.getTabAt(2)?.setIcon(R.drawable.tab_map)
                    }
                    1 -> {
                        tabLayer!!.getTabAt(0)?.setIcon(R.drawable.tab_list)
                        tabLayer!!.getTabAt(1)?.setIcon(R.drawable.tab_timeline_select)
                        tabLayer!!.getTabAt(2)?.setIcon(R.drawable.tab_map)
                    }
                    2 -> {
                        tabLayer!!.getTabAt(0)?.setIcon(R.drawable.tab_list)
                        tabLayer!!.getTabAt(1)?.setIcon(R.drawable.tab_timeline)
                        tabLayer!!.getTabAt(2)?.setIcon(R.drawable.tab_map_select)
                    }
                }
                if (position == 0) {
                    (getSupportFragmentManager()
                            .findFragmentByTag(
                                    "android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(
                                            position
                                    )
                            )
                            as Fragment1).refresh()
                }
                if (position == 1) {
                    gps_check.isChecked = false
                    (getSupportFragmentManager()
                            .findFragmentByTag(
                                    "android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(
                                            position
                                    )
                            )
                            as Fragment2).init()
                } else if (position == 2) {
                    (getSupportFragmentManager()
                            .findFragmentByTag(
                                    "android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(
                                            position
                                    )
                            )
                            as Fragment4).refresh()
                }
            }
        })
        tabLayer!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        tabLayer!!.getTabAt(0)?.setIcon(R.drawable.tab_list_select)
                        tabLayer!!.getTabAt(1)?.setIcon(R.drawable.tab_timeline)
                        tabLayer!!.getTabAt(2)?.setIcon(R.drawable.tab_map)
                        sd_imgBtn3.visibility = View.VISIBLE
                    }
                    1 -> {
                        tabLayer!!.getTabAt(0)?.setIcon(R.drawable.tab_list)
                        tabLayer!!.getTabAt(1)?.setIcon(R.drawable.tab_timeline_select)
                        tabLayer!!.getTabAt(2)?.setIcon(R.drawable.tab_map)
                        sd_imgBtn3.visibility = View.VISIBLE
                    }
                    2 -> {
                        tabLayer!!.getTabAt(0)?.setIcon(R.drawable.tab_list)
                        tabLayer!!.getTabAt(1)?.setIcon(R.drawable.tab_timeline)
                        tabLayer!!.getTabAt(2)?.setIcon(R.drawable.tab_map_select)
                        sd_imgBtn3.visibility = View.INVISIBLE
                    }
                }
                sd_viewPager.currentItem = tab.position
                tabPos = tab.position
                if (tab.position == 0) {
                    (getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(tab.position))
                            as Fragment1).refresh()
                }
                if (tab.position == 1) {
                    gps_check.isChecked = false
                    (getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(tab.position))
                            as Fragment2).init()
                } else if (tab.position == 2) {
                    (getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(tab.position))
                            as Fragment4).refresh()
                }
            }
        })

        sd_leftImg.setOnClickListener {
            if (DayNum > 1) {
                var intentToNext = Intent(this, ShowDateActivity::class.java)
                intentToNext.putExtra("ListID", ListID)
                intentToNext.putExtra("DayNum", DayNum - 1)
                startActivity(intentToNext)
                overridePendingTransition(
                        konkukSW.MP2019.roadline.R.anim.anim_slide_in_left,
                        konkukSW.MP2019.roadline.R.anim.anim_slide_out_right
                )
                finish()
            }
        }

        sd_rightImg.setOnClickListener {
            if (DayNum < maxDayNum) {
                var intentToNext = Intent(this, ShowDateActivity::class.java)
                intentToNext.putExtra("ListID", ListID)
                intentToNext.putExtra("DayNum", DayNum + 1)
                startActivity(intentToNext)
                overridePendingTransition(
                        R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_left

                )
                finish()
            }
        }

        sd_imgBtn1.setOnClickListener {
            //사진첩 버튼
            var Intent = Intent(this, ShowPhotoActivity::class.java)
            Intent.putExtra("ListID", ListID)
            Intent.putExtra("DayNum", DayNum) // 0:모든 Day 사진첩 전체 출력/ 1이상이면 그것만 출력
            startActivity(Intent)
            overridePendingTransition(
                    R.anim.anim_slide_in_top,
                    R.anim.anim_slide_out_bottom
            )
        }

        sd_imgBtn2.setOnClickListener {
            //가계부 버튼
            var PDIntentToMoney = Intent(this, ShowMoneyActivity::class.java)
            PDIntentToMoney.putExtra("ListID", ListID)
            PDIntentToMoney.putExtra("DayNum", DayNum) // 0:모든 Day 가계부 전체 출력/ 1이상이면 그것만 출력
            startActivity(PDIntentToMoney)
            overridePendingTransition(
                    R.anim.anim_slide_in_top,
                    R.anim.anim_slide_out_bottom
            )
        }

        sd_imgBtn3.setOnClickListener {

            var bitmap2: Bitmap? = null
            if (tabPos == 0) { //Fragment1
                sd_imgBtn3.visibility = View.VISIBLE
                bitmap2 = (getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(0))
                        as Fragment1).getScreenshot()

            }
            else if(tabPos == 1){ //Fragment2
                sd_imgBtn3.visibility = View.VISIBLE
                bitmap2 = (getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(1))
                        as Fragment2).getScreenshotFromRecyclerView()
            }

            if(bitmap2 != null){
                val storage = this.cacheDir
                val fileName = "temp.jpg"
                val tempFile = File(storage, fileName)
                try {
                    tempFile.createNewFile()
                    val out = FileOutputStream(tempFile)
                    bitmap2?.compress(Bitmap.CompressFormat.JPEG,100, out)
                    out.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                Log.v("tag", tempFile.toURI().toString())
                var uri = FileProvider.getUriForFile(this,"konkukSW.MP2019.roadline.fileprovider",tempFile)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.addCategory(Intent.CATEGORY_DEFAULT)
                shareIntent.type = "image/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(shareIntent, "여행 일정 공유"))
            }
        }
    }
}





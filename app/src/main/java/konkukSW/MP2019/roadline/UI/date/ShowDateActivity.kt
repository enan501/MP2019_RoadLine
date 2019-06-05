package konkukSW.MP2019.roadline.UI.date

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.TabAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import kotlinx.android.synthetic.main.activity_show_date.*
import com.kakao.util.KakaoParameterException
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder
import com.kakao.kakaolink.KakaoLink
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.fragment_fragment2.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import android.util.DisplayMetrics




class ShowDateActivity : AppCompatActivity() {


    var ListID = "a"
    var DayNum = 0
    var maxDayNum = 0
    lateinit var adapter:TabAdapter
    lateinit var realm: Realm
    private var tabLayer:TabLayout?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_show_date)
        init()
    }

    fun init(){
        initData()
        initListener()
    }
    fun back(v: View?):Unit{
        finish()
    }

    fun initData(){
        tabLayer = findViewById(konkukSW.MP2019.roadline.R.id.sd_layout_tab)
        tabLayer!!.addTab(tabLayer!!.newTab().setIcon(konkukSW.MP2019.roadline.R.drawable.tab_list_select))
        tabLayer!!.addTab(tabLayer!!.newTab().setIcon(konkukSW.MP2019.roadline.R.drawable.tab_timeline))
        tabLayer!!.addTab(tabLayer!!.newTab().setIcon(konkukSW.MP2019.roadline.R.drawable.tab_map))

        ListID = intent.getStringExtra("ListID")
        DayNum = intent.getIntExtra("DayNum", 0)
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        maxDayNum = realm.where<T_Day>(T_Day::class.java).equalTo("listID",ListID).findAll().size
        sd_textView1.setText(realm.where<T_List>(T_List::class.java).equalTo("id",ListID).findFirst()!!.title + " - DAY " + DayNum.toString())
        sd_textView2.setText(realm.where<T_Day>(T_Day::class.java).equalTo("listID",ListID).equalTo("num",DayNum).findFirst()!!.date)
    }

    fun initListener(){
        adapter = TabAdapter(supportFragmentManager, tabLayer!!.tabCount)
        sd_viewPager.adapter = adapter
        //sd_viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayer))
        sd_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                tabLayer!!.getTabAt(0)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_list)
                tabLayer!!.getTabAt(1)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_timeline)
                tabLayer!!.getTabAt(2)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_map)
                when(position) {
                    0   ->    tabLayer!!.getTabAt(0)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_list_select)
                    1   ->    tabLayer!!.getTabAt(1)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_timeline_select)
                    2   ->    tabLayer!!.getTabAt(2)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_map_select)
                }
            }
            override fun onPageSelected(position: Int) {
                if(position == 1) {
                    (getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(position))
                            as Fragment2).init()
                }
                else if(position == 2) {
                    (getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(position))
                            as Fragment4).refresh()
                }
            }
        })
        tabLayer!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabLayer!!.getTabAt(0)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_list)
                tabLayer!!.getTabAt(1)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_timeline)
                tabLayer!!.getTabAt(2)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_map)
                when(tab.position) {
                    0   ->    tabLayer!!.getTabAt(0)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_list_select)
                    1   ->    tabLayer!!.getTabAt(1)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_timeline_select)
                    2   ->    tabLayer!!.getTabAt(2)?.setIcon(konkukSW.MP2019.roadline.R.drawable.tab_map_select)
                }
                sd_viewPager.currentItem = tab.position
                if(tab.position == 1) {
                    (getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(tab.position))
                            as Fragment2).init()
                }
                else if(tab.position == 2) {
                    (getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + sd_viewPager.getId() + ":" + adapter.getItemId(tab.position))
                            as Fragment4).refresh()
                }
            }
        })

        sd_leftImg.setOnClickListener {
            if(DayNum>1) {
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
            if(DayNum < maxDayNum) {
                var intentToNext = Intent(this, ShowDateActivity::class.java)
                intentToNext.putExtra("ListID", ListID)
                intentToNext.putExtra("DayNum", DayNum + 1)
                startActivity(intentToNext)
                overridePendingTransition(
                    konkukSW.MP2019.roadline.R.anim.anim_slide_in_right,
                    konkukSW.MP2019.roadline.R.anim.anim_slide_out_left

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
                konkukSW.MP2019.roadline.R.anim.anim_slide_in_top,
                konkukSW.MP2019.roadline.R.anim.anim_slide_out_bottom
            )
        }

        sd_imgBtn2.setOnClickListener {
            //가계부 버튼
            var PDIntentToMoney = Intent(this, ShowMoneyActivity::class.java)
            PDIntentToMoney.putExtra("ListID", ListID)
            PDIntentToMoney.putExtra("DayNum", DayNum) // 0:모든 Day 가계부 전체 출력/ 1이상이면 그것만 출력
            startActivity(PDIntentToMoney)
            overridePendingTransition(
                konkukSW.MP2019.roadline.R.anim.anim_slide_in_top,
                konkukSW.MP2019.roadline.R.anim.anim_slide_out_bottom
            )
        }

        sd_imgBtn3.setOnClickListener {
            //공유 버튼
            try {
                sd_leftImg.visibility = View.INVISIBLE
                sd_rightImg.visibility = View.INVISIBLE
                captureLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val dm = resources.displayMetrics
                val width = dm.widthPixels
                val bitmap = Bitmap.createBitmap(width, captureLayout.measuredHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val bgDrawable = captureLayout.background
                if (bgDrawable != null) {
                    bgDrawable.draw(canvas)
                } else {
                    canvas.drawColor(Color.WHITE)
                }
                captureLayout.draw(canvas)
                timeline_imageView.setImageBitmap(bitmap)
                sd_leftImg.visibility = View.VISIBLE
                sd_rightImg.visibility = View.VISIBLE

                val storage = this.cacheDir
                val fileName = "temp.jpg"
                val tempFile = File(storage, fileName)
                try{
                    tempFile.createNewFile()
                    val out = FileOutputStream(tempFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    out.close()
                } catch (e: FileNotFoundException){
                    e.printStackTrace()
                } catch (e:IOException){
                    e.printStackTrace()
                }
                Log.v("tag", tempFile.absolutePath)

                val kakaoLink = KakaoLink.getKakaoLink(applicationContext)
                val messageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder()
                messageBuilder.addImage(tempFile.absolutePath, bitmap.width, bitmap.height)
                kakaoLink.sendMessage(messageBuilder, this)

            } catch (e: KakaoParameterException) {
                e.printStackTrace()
            }
        }

    }

}

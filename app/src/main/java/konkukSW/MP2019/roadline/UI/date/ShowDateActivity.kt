package konkukSW.MP2019.roadline.UI.date

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import konkukSW.MP2019.roadline.R
import android.support.design.widget.TabLayout
import konkukSW.MP2019.roadline.Data.Adapter.TabAdapter
import kotlinx.android.synthetic.main.activity_show_date.*
import android.content.Intent
import konkukSW.MP2019.roadline.Data.Dataclass.Spot
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity


class ShowDateActivity : AppCompatActivity() {

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

    fun initData(){
        tabLayer = findViewById(konkukSW.MP2019.roadline.R.id.sd_layout_tab)
        tabLayer!!.addTab(tabLayer!!.newTab().setText("리스트"))
        tabLayer!!.addTab(tabLayer!!.newTab().setText("가로 타임라인"))
        tabLayer!!.addTab(tabLayer!!.newTab().setText("세로 타임라인"))
        tabLayer!!.addTab(tabLayer!!.newTab().setText("지도"))

    }

    fun initListener(){
        val adapter = TabAdapter(supportFragmentManager, tabLayer!!.tabCount)
        sd_viewPager.adapter = adapter

        sd_viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayer))

        tabLayer!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                sd_viewPager.currentItem = tab.position
            }
        })

        sd_leftImg.setOnClickListener {
            startActivity(Intent(this, ShowDateActivity::class.java))
            overridePendingTransition(konkukSW.MP2019.roadline.R.anim.anim_slide_in_right, konkukSW.MP2019.roadline.R.anim.anim_slide_out_left)
            finish()
        }

        sd_rightImg.setOnClickListener {
            startActivity(Intent(this, ShowDateActivity::class.java))
            overridePendingTransition(konkukSW.MP2019.roadline.R.anim.anim_slide_in_left, konkukSW.MP2019.roadline.R.anim.anim_slide_out_right)
            finish()
        }

        sd_imgBtn1.setOnClickListener {
            //사진첩 버튼
            startActivity(Intent(this, ShowPhotoActivity::class.java))
        }

        sd_imgBtn2.setOnClickListener {
            //가계부 버튼
            startActivity(Intent(this, ShowMoneyActivity::class.java))
        }

        sd_imgBtn3.setOnClickListener {
            //공유 버튼
        }

    }

}

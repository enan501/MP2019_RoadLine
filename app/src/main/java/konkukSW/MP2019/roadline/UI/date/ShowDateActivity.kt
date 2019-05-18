package konkukSW.MP2019.roadline.UI.date

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import konkukSW.MP2019.roadline.R
import android.support.design.widget.TabLayout
import konkukSW.MP2019.roadline.Data.TabAdapter
import kotlinx.android.synthetic.main.activity_show_date.*

class ShowDateActivity : AppCompatActivity() {

    private var tabLayer:TabLayout?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_date)
        init()
    }

    fun init(){
        initData()
        initListener()
    }

    fun initData(){
        tabLayer = findViewById(R.id.sd_layout_tab)
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

        }

        sd_rightImg.setOnClickListener {

        }

    }

}

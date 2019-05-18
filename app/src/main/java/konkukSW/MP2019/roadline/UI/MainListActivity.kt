package konkukSW.MP2019.roadline.UI

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import konkukSW.MP2019.roadline.Data.Adapter.MainListAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.MainList
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_main_list.*

class MainListActivity : AppCompatActivity() {

    var data:ArrayList<MainList> = arrayListOf(MainList("대만여행","2019.02.10 ~ 2019.02.14",""),
            MainList("일본여행","2019.02.10 ~ 2019.02.14",""),
            MainList("우주여행","2019.02.10 ~ 2019.02.14",""),
            MainList("집여행","2019.02.10 ~ 2019.02.14","")
        )
    lateinit var MLAdapter:MainListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)
        init()
    }
    fun init(){
        initLayout()
    }
    fun initLayout(){
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        ML_rView.layoutManager = layoutManager
        MLAdapter = MainListAdapter(data)
        ML_rView.adapter = MLAdapter
    }
}

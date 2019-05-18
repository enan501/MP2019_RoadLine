package konkukSW.MP2019.roadline.UI.date

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import konkukSW.MP2019.roadline.Data.Adapter.PickDateAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.PickDate
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_pick_date.*

class PickDateActivity : AppCompatActivity() {

    var data:ArrayList<PickDate> = arrayListOf(
        PickDate(1,"2019.02.10"),
        PickDate(2,"2019.02.11"),
        PickDate(3,"2019.02.12"),
        PickDate(4,"2019.02.13"),
        PickDate(5,"2019.02.14"),
        PickDate(6,"2019.02.15"),
        PickDate(7,"2019.02.16")
    )
    lateinit var PDAdapter: PickDateAdapter
    var snapHelper = LinearSnapHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_date)
        init()
    }
    fun init(){
        initLayout()
    }
    fun initLayout(){
        val layoutManager = CenterZoomLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        PD_rView.layoutManager = layoutManager
        PDAdapter = PickDateAdapter(data)
        PD_rView.adapter = PDAdapter


        snapHelper.attachToRecyclerView(PD_rView)
    }
    fun notifySnapPositionChanged(rView:RecyclerView){
        val snapView = snapHelper.findSnapView(PD_rView.layoutManager)
        val snapPosition = PD_rView.layoutManager!!.getPosition(snapView!!)
    }
}

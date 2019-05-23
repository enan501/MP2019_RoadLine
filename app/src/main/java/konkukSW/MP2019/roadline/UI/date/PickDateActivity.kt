package konkukSW.MP2019.roadline.UI.date

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.LinearSnapHelper
import konkukSW.MP2019.roadline.Data.Adapter.PickDateAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.PickDate
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import kotlinx.android.synthetic.main.activity_pick_date.*



class PickDateActivity : AppCompatActivity() {

    var title:String = ""
    var data:ArrayList<PickDate> = arrayListOf(
        PickDate(0,"first"),
        PickDate(1,"2019.02.10"),
        PickDate(2,"2019.02.11"),
        PickDate(3,"2019.02.12"),
        PickDate(4,"2019.02.13"),
        PickDate(5,"2019.02.14"),
        PickDate(6,"2019.02.15"),
        PickDate(7,"2019.02.16"),
        PickDate(0,"last")
    )
    lateinit var PDAdapter: PickDateAdapter
    var snapHelper = LinearSnapHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_pick_date)
        init()

    }
    fun init(){
        initLayout()
    }
    fun initLayout(){
        val layoutManager = CenterZoomLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val smoothScroller = object : LinearSmoothScroller(this) {
            override fun getHorizontalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = 1
        smoothScroller.computeScrollVectorForPosition(0)
        PD_rView.layoutManager = layoutManager
        PDAdapter = PickDateAdapter(data)
        PD_rView.adapter = PDAdapter
        layoutManager.startSmoothScroll(smoothScroller)
        snapHelper.attachToRecyclerView(PD_rView) //아이템 가운데로 끌어 맞추기
        addListener()

    }
    fun addListener() {
        PDAdapter.itemClickListener = object : PickDateAdapter.OnItemClickListener {
            override fun OnItemClick(holder: PickDateAdapter.ViewHolder, data: PickDate, position: Int) {
                var PDIntentToSD = Intent(applicationContext, ShowDateActivity::class.java)
                PDIntentToSD.putExtra("title",title)
                PDIntentToSD.putExtra("day",data.day)
                startActivity(PDIntentToSD)
            }
        }
        PD_photoBtn.setOnClickListener {
            var PDIntentToPhoto = Intent(applicationContext, ShowPhotoActivity::class.java)
            PDIntentToPhoto.putExtra("title",title)
            PDIntentToPhoto.putExtra("day",0) //날짜별 사진or가계부는 Intent로 day값 넘겨서 하고, 0이면 전체 출력해주는걸로 하면 어떨까요
            startActivity(PDIntentToPhoto)
        }
        PD_moneyBtn.setOnClickListener {
            var PDIntentToMoney = Intent(applicationContext, ShowMoneyActivity::class.java)
            PDIntentToMoney.putExtra("title",title)
            PDIntentToMoney.putExtra("day",0) //날짜별 사진or가계부는 Intent로 day값 넘겨서 하고, 0이면 전체 출력해주는걸로 하면 어떨까요
            startActivity(PDIntentToMoney)
        }
    }

/*    fun notifySnapPositionChanged(rView:RecyclerView){
        val snapView = snapHelper.findSnapView(PD_rView.layoutManager)
        val snapPosition = PD_rView.layoutManager!!.getPosition(snapView!!)
    }*/
}

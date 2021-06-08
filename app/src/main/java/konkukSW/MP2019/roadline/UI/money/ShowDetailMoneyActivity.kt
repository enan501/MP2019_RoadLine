package konkukSW.MP2019.roadline.UI.money

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.charts.PieChart
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.CategoryPrintAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.Dataclass.CategoryTotal
import kotlinx.android.synthetic.main.activity_show_detail_money.*
import java.text.DecimalFormat
import kotlin.collections.ArrayList


class ShowDetailMoneyActivity : AppCompatActivity() {
    lateinit var adapterList: ArrayList<CategoryTotal>
    private val categoryList: Array<String> = arrayOf("식사", "쇼핑", "교통", "관광", "숙박", "기타")
    lateinit var priceList: Array<Double> //카테고리별 가격 합
    lateinit var pricePercentList: Array<Int> //카테고리별 가격 퍼센트
    lateinit var realm: Realm
    private val shortFormat = DecimalFormat("###,### 원")
    private val colors = arrayOf(Color.rgb(95,157,212),
            Color.rgb(162, 211, 223),
            Color.rgb(96, 176, 214),
            Color.rgb(96, 174, 191),
            Color.rgb(140, 207, 223),
            Color.rgb(96, 209, 214),
            Color.rgb(92, 204, 192))

    var ListID = ""
    var DayNum = 0
    var totalMoneyValue = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_show_detail_money)
        initToolbar()
        initData()
        initLayout()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
    }

    fun initData(){
        val i = intent
        ListID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum", 0)

        priceList = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        pricePercentList = arrayOf(0, 0, 0, 0, 0, 0)

        Realm.init(this)
        realm = Realm.getDefaultInstance()
        var results:RealmResults<T_Money>
        if (DayNum == 0){ //날짜 전체
            results = realm.where(T_Money::class.java).equalTo("listID", ListID).findAll()
        }
        else{
            results = realm.where(T_Money::class.java).equalTo("listID", ListID).equalTo("dayNum", DayNum).findAll()
        }
        totalMoneyValue = 0.0
        for (i in results) {
            when (i!!.cate) {
                "식사" -> priceList[0] += i.price * i.currency!!.rate
                "쇼핑" -> priceList[1] += i.price * i.currency!!.rate
                "교통" -> priceList[2] += i.price * i.currency!!.rate
                "관광" -> priceList[3] += i.price * i.currency!!.rate
                "숙박" -> priceList[4] += i.price * i.currency!!.rate
                "기타" -> priceList[5] += i.price * i.currency!!.rate
            }
            totalMoneyValue += i.price * i.currency!!.rate
        }

        for (i in 0 until categoryList.size) {
            val percent = (priceList[i] / totalMoneyValue) * 100
            pricePercentList[i] = Math.round(percent.toFloat())
        }

        //listView Data
        adapterList = ArrayList()
        for (i in 0..5)
            adapterList.add(CategoryTotal(categoryList[i], shortFormat.format(priceList[i])))
    }

    fun initLayout(){
        if(DayNum == 0) {
            day.visibility = View.GONE
        } else {
            day.visibility = View.VISIBLE
            day.text = "DAY"+ DayNum.toString()
        }
        totalMoney.text = shortFormat.format(totalMoneyValue)

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5F, 10F, 5F, 5F)
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isDrawHoleEnabled = false
        pieChart.setHoleColor(Color.WHITE)
        pieChart.transparentCircleRadius = 61f

        val chartValues = ArrayList<PieEntry>()
        val chartColors: ArrayList<Int> = ArrayList()
        for (i in 0 until categoryList.size){
            if(pricePercentList[i] != 0){
                chartValues.add(PieEntry(pricePercentList[i].toFloat(), categoryList[i]))
                chartColors.add(colors[i])
            }
        }
        pieChart.animateY(1000, Easing.EaseInOutCubic) //애니메이션

        val dataSet = PieDataSet(chartValues, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = chartColors

        val data = PieData(dataSet)
        data.setValueTextSize(18f)
        data.setValueTextColor(Color.rgb(82,82,82))

        pieChart.data = data

        categoryMoneyListView.adapter = CategoryPrintAdapter(this, konkukSW.MP2019.roadline.R.layout.row_category_money, adapterList)
    }

}

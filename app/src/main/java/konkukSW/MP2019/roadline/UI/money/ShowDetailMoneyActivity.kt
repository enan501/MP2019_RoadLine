package konkukSW.MP2019.roadline.UI.money

import android.support.v7.app.AppCompatActivity
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
import konkukSW.MP2019.roadline.Data.Adapter.CategoryPrintAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.Dataclass.CategoryToatal
import kotlinx.android.synthetic.main.activity_show_detail_money.*
import kotlin.collections.ArrayList


class ShowDetailMoneyActivity : AppCompatActivity() {
    lateinit var adapterList: ArrayList<CategoryToatal>
    lateinit var adapter: CategoryPrintAdapter
    var categoryList: Array<String> = arrayOf("식사", "쇼핑", "교통", "관광", "숙박", "기타")
    var priceList: ArrayList<Int> = ArrayList()
    var pricePercentList: ArrayList<Float> = ArrayList()

    var ListID = ""
    var DayNum = 0
    var dayCount = 0
    var toatalMoneyValue = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_show_detail_money)
        //initDB()

        val i = getIntent()
        ListID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum", 0)
        initToolbar()
        showPieChart()
        showCategoryList()
        showLineChart()
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

    fun initDB() {
        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

    fun showPieChart() {
        for (i in 0..5)
            priceList.add(0) // 초기화

        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val DBlist = realm.where(T_Money::class.java)
            .equalTo("listID", ListID)
            .findAll() // 현재 리스트의 가계부만 들고옴

        if (DayNum == 0) // 리스트내 Day 전부 다 출력
        {
            for (i in 0..DBlist.size - 1) { // 디비에서 값 불러옴
                when (DBlist.get(i)!!.cate.toString()) {
                    "식사" -> priceList[0] += DBlist.get(i)!!.price.toInt()
                    "쇼핑" -> priceList[1] += DBlist.get(i)!!.price.toInt()
                    "교통" -> priceList[2] += DBlist.get(i)!!.price.toInt()
                    "관광" -> priceList[3] += DBlist.get(i)!!.price.toInt()
                    "숙박" -> priceList[4] += DBlist.get(i)!!.price.toInt()
                    "기타" -> priceList[5] += DBlist.get(i)!!.price.toInt()
                }
            }
        } else { // 한개의 Day만 출력
            val DayDBlist = realm.where(T_Money::class.java)
                .equalTo("listID", ListID)
                .equalTo("dayNum", DayNum)
                .findAll() // 현재 리스트의 가계부만 들고옴
            for (i in 0..DayDBlist.size - 1) { // 디비에서 값 불러옴
                when (DayDBlist.get(i)!!.cate.toString()) {
                    "식사" -> priceList[0] += DayDBlist.get(i)!!.price.toInt()
                    "쇼핑" -> priceList[1] += DayDBlist.get(i)!!.price.toInt()
                    "교통" -> priceList[2] += DayDBlist.get(i)!!.price.toInt()
                    "관광" -> priceList[3] += DayDBlist.get(i)!!.price.toInt()
                    "숙박" -> priceList[4] += DayDBlist.get(i)!!.price.toInt()
                    "기타" -> priceList[5] += DayDBlist.get(i)!!.price.toInt()
                }
            }
        }


        for (i in 0..5)
            toatalMoneyValue += priceList[i]

        if(DayNum == 0) {
            day.visibility = View.GONE
        } else {
            day.visibility = View.VISIBLE
            day.text = "DAY"+DayNum.toString()
        }
        totalMoney.text = toatalMoneyValue.toInt().toString() + "원" // 총지출액

        for (i in 0..5) { // 총지출액 대비 카테고리금액 퍼센트로 통계
            println("가격 : " + priceList[i])
            if (priceList[i] == 0) {
                pricePercentList.add(0F)
            } else {
                val percent = (priceList[i] / toatalMoneyValue) * 100
                println("퍼센트 : " + percent)
                pricePercentList.add(percent.toFloat())
            }
        }

        var pieChart = findViewById(konkukSW.MP2019.roadline.R.id.piechart) as PieChart

        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5F, 10F, 5F, 5F)

        pieChart.setDragDecelerationFrictionCoef(0.95f)

        pieChart.setDrawHoleEnabled(false)
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleRadius(61f)

        val chartValues = ArrayList<PieEntry>()

        for (i in 0..5)
            chartValues.add(PieEntry(pricePercentList[i], categoryList[i]))

        val colors: ArrayList<Int> = ArrayList()

        colors.add(Color.rgb(95,157,212))
        colors.add(Color.rgb(162, 211, 223))
        colors.add(Color.rgb(96, 176, 214))
        colors.add(Color.rgb(96, 174, 191))
        colors.add(Color.rgb(140, 207, 223))
        colors.add(Color.rgb(96, 209, 214))
        colors.add(Color.rgb(92, 204, 192))

        pieChart.animateY(1000, Easing.EaseInOutCubic) //애니메이션

        val dataSet = PieDataSet(chartValues, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.setColors(colors)

        val data = PieData(dataSet)
        data.setValueTextSize(18f)
        data.setValueTextColor(Color.rgb(82,82,82))

        pieChart.setData(data)
    }

    fun showCategoryList() {
        adapterList = ArrayList()

        for (i in 0..5)
            adapterList.add(CategoryToatal(categoryList[i], priceList[i].toString() + "원"))

        adapter = CategoryPrintAdapter(this, konkukSW.MP2019.roadline.R.layout.row_category_money, adapterList)
        categoryMoneyListView.adapter = adapter
    }

    fun showLineChart() {

    }
}

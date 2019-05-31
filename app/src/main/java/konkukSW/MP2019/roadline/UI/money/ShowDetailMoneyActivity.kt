package konkukSW.MP2019.roadline.UI.money

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import konkukSW.MP2019.roadline.R
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.animation.Easing.EaseInOutCubic
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.charts.PieChart
import android.graphics.Color
import com.github.mikephil.charting.components.Description
import konkukSW.MP2019.roadline.Data.Adapter.CategoryPrintAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.CategoryToatal
import kotlinx.android.synthetic.main.activity_show_detail_money.*
import java.util.*
import kotlin.collections.ArrayList


class ShowDetailMoneyActivity : AppCompatActivity() {
    lateinit var categoryList:ArrayList<CategoryToatal>
    lateinit var adapter:CategoryPrintAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_show_detail_money)
        showPieChart()
        showCategoryList()
        showLineChart()
    }

    fun showPieChart() {
        var pieChart = findViewById(konkukSW.MP2019.roadline.R.id.piechart) as PieChart


        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5F, 10F, 5F, 5F)

        pieChart.setDragDecelerationFrictionCoef(0.95f)

        pieChart.setDrawHoleEnabled(false)
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleRadius(61f)

        val yValues = ArrayList<PieEntry>()

        yValues.add(PieEntry(34f, "식사"))
        yValues.add(PieEntry(23f, "쇼핑"))
        yValues.add(PieEntry(14f, "교통"))
        yValues.add(PieEntry(35f, "관광"))
        yValues.add(PieEntry(40f, "숙박"))
        yValues.add(PieEntry(40f, "기타"))

        val colors:ArrayList<Int> = ArrayList()
        colors.add(Color.rgb(156, 254, 230))
        colors.add(Color.rgb(159, 185, 235))
        colors.add(Color.rgb(143, 231, 161))
        colors.add(Color.rgb(160, 239, 136))
        colors.add(Color.rgb(200, 246, 139))
        colors.add(Color.rgb(176, 219, 233))
        colors.add(Color.rgb(183, 176, 253))

        pieChart.animateY(1000, Easing.EaseInOutCubic) //애니메이션

        val dataSet = PieDataSet(yValues, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.setColors(colors)

        val data = PieData(dataSet)
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.BLACK)

        pieChart.setData(data)
    }

    fun showCategoryList() {
        categoryList = ArrayList()

        categoryList.add(CategoryToatal("식사", "18390원"))
        categoryList.add(CategoryToatal("쇼핑", "22012원"))
        categoryList.add(CategoryToatal("교통", "6200원"))
        categoryList.add(CategoryToatal("관광", "11720원"))
        categoryList.add(CategoryToatal("숙박", "32090원"))
        categoryList.add(CategoryToatal("기타", "2900원"))

        adapter = CategoryPrintAdapter(this, konkukSW.MP2019.roadline.R.layout.row_category_money, categoryList)
        categoryMoneyListView.adapter = adapter
    }

    fun showLineChart() {

    }
}

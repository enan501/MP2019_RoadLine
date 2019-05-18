package konkukSW.MP2019.roadline.UI.money

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import konkukSW.MP2019.roadline.Data.Adapter.MoneyItemAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_show_money.*

var data:ArrayList<MoneyItem> = ArrayList()
lateinit var adapter:MoneyItemAdapter
var dayCount = 0;

class ShowMoneyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_money)
        initLayout()
        initSwipe()

    }

    fun initSwipe() {
        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                adapter.moveItem(p1.adapterPosition, p2.adapterPosition)
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                adapter.removeItem(p0.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(money_recycleView)
    }

    fun initLayout() {
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL) // 크기 제각각 가능
        money_recycleView.layoutManager = layoutManager
        adapter = MoneyItemAdapter(data)
        money_recycleView.adapter = adapter
        for(i in 0..dayCount)
        {
            data.add(MoneyItem("null", 0, "null", 0))
            data.add(MoneyItem("null", 0, "null", 1))
            data.add(MoneyItem("null", 0, "null", 1))

            data.add(MoneyItem("3000", 1, "null", 2))
            data.add(MoneyItem("6000", 2, "null", 2))
            data.add(MoneyItem("6000", 2, "null", 2))
            data.add(MoneyItem("3000", 1, "null", 2))
            data.add(MoneyItem("6000", 2, "null", 2))
            data.add(MoneyItem("6000", 2, "null", 2))

            data.add(MoneyItem("null", 0, "null", 1))
            data.add(MoneyItem("null", 0, "null", 1))
            data.add(MoneyItem("Total:1000", 0, "null", 3))
        }
    }

}



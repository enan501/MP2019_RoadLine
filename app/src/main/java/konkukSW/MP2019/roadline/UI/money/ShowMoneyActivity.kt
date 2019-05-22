package konkukSW.MP2019.roadline.UI.money

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
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
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.util.Log

var data:ArrayList<MoneyItem> = ArrayList()
var dataSize:Array<Int> = arrayOf(0, 0, 0, 0, 0)
var dataMaxPos:Array<Int> = arrayOf(0, 0, 0, 0, 0)

lateinit var adapter:MoneyItemAdapter
var dayCount = 2;

class ShowMoneyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_money)
        initLayout()
        //initSwipe()

        /* 리사이클뷰 어댑터에 리스너 달기 */
        adapter.itemLongClickListener = object : MoneyItemAdapter.OnItemLongClickListener{
            override fun OnItemLongClick(holder: MoneyItemAdapter.ViewHolder1, view: View, item: MoneyItem, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Toast.makeText(applicationContext, item.price.toString(), Toast.LENGTH_LONG).show()

                val alert_confirm = AlertDialog.Builder(this@ShowMoneyActivity)
                alert_confirm.setMessage("삭제할래?").setCancelable(false).setPositiveButton("취소",
                   DialogInterface.OnClickListener { dialog, which ->
                        // content
                    }).setNegativeButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        data.removeAt(position)
                        data.add(dataMaxPos[item.day]-4, MoneyItem("null", 0, "null", item.day,2))
                        var emptyCount = 0
                        if(data.get(dataMaxPos[item.day]-4).viewType == 2)
                            emptyCount++
                        if(data.get(dataMaxPos[item.day]-5).viewType == 2)
                            emptyCount++
                        if(data.get(dataMaxPos[item.day]-6).viewType == 2)
                            emptyCount++
                        System.out.println(emptyCount)
                        if(emptyCount == 3) {
                            data.removeAt(dataMaxPos[item.day] - 4)
                            data.removeAt(dataMaxPos[item.day] - 5)
                            data.removeAt(dataMaxPos[item.day] - 6)
                            dataSize[item.day]-=3
                            dataMaxPos[item.day]-=3
                        }
                        adapter.notifyDataSetChanged()
                        return@OnClickListener
                    })
                val alert = alert_confirm.create()
                alert.show()
            }

        }
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
        val layoutManager = GridLayoutManager(this, 3)
        money_recycleView.layoutManager = layoutManager
        adapter = MoneyItemAdapter(data)
        money_recycleView.adapter = adapter
        for(i in 0..dayCount)
        {
            data.add(MoneyItem("null", 0, "null", i,0))
            data.add(MoneyItem("null", 0, "null", i,2))
            data.add(MoneyItem("null", 0, "null", i,4))

            data.add(MoneyItem("3000", 1, "null", i,1))
            data.add(MoneyItem("6000", 2, "null", i,1))
            data.add(MoneyItem("6000", 2, "null", i,1))
            data.add(MoneyItem("3000", 1, "null", i,1))
            data.add(MoneyItem("6000", 2, "null", i,1))
            data.add(MoneyItem("6000", 2, "null", i,1))

            data.add(MoneyItem("null", 0, "null", i,2))
            data.add(MoneyItem("null", 0, "null", i,2))
            data.add(MoneyItem("Total:1000", 0, "null", i,3))

            dataSize[i] = 12;
            dataMaxPos[i] += data.size
        }
        adapter.notifyDataSetChanged()
    }

}



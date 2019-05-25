package konkukSW.MP2019.roadline.UI.money

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import konkukSW.MP2019.roadline.Data.Adapter.MoneyItemAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_show_money.*
import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewManager
import android.widget.*

var data:ArrayList<MoneyItem> = ArrayList()

lateinit var adapter:MoneyItemAdapter

var ListNumber = 0; // 이건 나중에 디비에서 받아와야함
var dayCount = 2; // 이것도 나중에 디비에서 받아와야함

var TotalPrice = 0;

class ShowMoneyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_money)
        initLayout()

        /* 리사이클뷰 어댑터에 리스너 달기 */
        adapter.itemLongClickListener = object : MoneyItemAdapter.OnItemLongClickListener{
            override fun OnItemLongClick(holder: MoneyItemAdapter.ViewHolder1, view: View, item: MoneyItem, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val alert_confirm = AlertDialog.Builder(this@ShowMoneyActivity)
                alert_confirm.setMessage("삭제할래?").setCancelable(false).setPositiveButton("취소",
                    DialogInterface.OnClickListener { dialog, which ->
                        // content
                    }).setNegativeButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        eraseItem(position, item)
                        return@OnClickListener
                    })
                val alert = alert_confirm.create()
                alert.show()
            }
        }
        adapter.itemClickListener = object : MoneyItemAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MoneyItemAdapter.ViewHolder4, view: View, item: MoneyItem, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                addItem(position, item.listNum, item.dayNum,3000, 0, R.drawable.testimg1, 1)
            }
        }
        adapter.itemClickListener2 = object : MoneyItemAdapter.OnItemClickListener2 {
            override fun OnItemClick2(holder: MoneyItemAdapter.ViewHolder1, view: View, item: MoneyItem, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                ShowLayout(item)
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
        for(i in 1..dayCount)
        {
            data.add(MoneyItem(ListNumber, i,20190530, 0, 0, 0))
            data.add(MoneyItem(ListNumber, i,-1, 0, 0, 2))
            data.add(MoneyItem(ListNumber, i,-1, 0, 0, 4))

            data.add(MoneyItem(ListNumber, i,-1, 0, 0, 5))
            data.add(MoneyItem(ListNumber, i,-1, 0, 0, 2))
            data.add(MoneyItem(ListNumber, i,0, 0, 0, 3))
        }
        adapter.notifyDataSetChanged()
    }

    fun eraseItem(position:Int, item:MoneyItem)
    {
        var removePrice = data.get(position).price
        data.removeAt(position)
        var lastPos = 0;
        for(i in 0..data.size)
        {
            if(data.get(i).dayNum == data.get(position).dayNum && data.get(i).viewType == 5) {
                lastPos = i
                break;
            }
        }
        data.add(lastPos, MoneyItem(item.listNum, item.dayNum,-1, 0, 0,2))
        if(data.get(lastPos).viewType == 2 &&
            data.get(lastPos-1).viewType == 2 &&
            data.get(lastPos-2).viewType == 2)
        {
            data.removeAt(lastPos)
            data.removeAt(lastPos-1)
            data.removeAt(lastPos-2)
        }
        for(i in 0..data.size) // 토탈에 방금 제거한 가격 빼주기
        {
            if(data.get(i).dayNum == data.get(position).dayNum && data.get(i).viewType == 3) {
                data.get(i).price -= removePrice
                TotalPrice += removePrice
                money_totalTextView.text = "Total " + TotalPrice.toString()
                break;
            }
        }
        adapter.notifyDataSetChanged()
    }
    fun addItem(position:Int, listNum:Int, dayNum:Int, price:Int, cate:Int, img:Int, viewType:Int)
    {
        var lastPos = 0;
        for(i in 0..data.size)
        {
            if(data.get(i).dayNum == data.get(position).dayNum && data.get(i).viewType == 5) {
                lastPos = i
                break;
            }
        }
        if(data.get(lastPos-1).viewType != 2) {
            data.add(lastPos, MoneyItem(listNum, dayNum, price, cate, img, viewType))
            data.add(lastPos+1, MoneyItem(listNum, dayNum,-1, 0, 0, 2))
            data.add(lastPos+2, MoneyItem(listNum, dayNum,-1, 0, 0, 2))
        }
        else if(data.get(lastPos-2).viewType == 2)
        {
            data.removeAt(lastPos-2)
            data.add(lastPos-2, MoneyItem(listNum, dayNum, price, cate, img, viewType))
        }
        else if(data.get(lastPos-1).viewType == 2)
        {
            data.removeAt(lastPos-1)
            data.add(lastPos-1, MoneyItem(listNum, dayNum, price, cate, img, viewType))
        }
        for(i in 0..data.size) // 토탈에 방금 추가한 가격 더해주기
        {
            if(data.get(i).dayNum == data.get(position).dayNum && data.get(i).viewType == 3) {
                data.get(i).price += price
                TotalPrice += price
                money_totalTextView.text = "Total " + TotalPrice.toString()
                break;
            }
        }
        adapter.notifyDataSetChanged()
    }
    fun ShowLayout(item:MoneyItem):Unit
    {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //레이아웃을 위에 겹쳐서 올리는 부분
        val ll = inflater.inflate(R.layout.detail_img, null) as LinearLayout //레이아웃 객체생성
        ll.setBackgroundColor(Color.parseColor("#99000000")) //레이아웃 배경 투명도 주기
        val paramll = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)
        addContentView(ll, paramll) //레이아웃 위에 겹치기
        ll.setOnClickListener{
            (ll.getParent() as ViewManager).removeView(ll)
        }
        var imageView = ll.findViewById<ImageView>(R.id.imageView) // 매번 새로운 레이어 이므로 ID를 find 해준다.
        imageView.setImageResource(item.img)
    }

}



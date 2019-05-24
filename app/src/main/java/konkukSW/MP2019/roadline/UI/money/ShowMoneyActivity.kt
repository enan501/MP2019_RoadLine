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
                addItem(position, "3000", 0, R.drawable.testimg1, item.day, 1)
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
            data.add(MoneyItem("null", 0, 0, i,0))
            data.add(MoneyItem("null", 0, 0, i,2))
            data.add(MoneyItem("null", 0, 0, i,4))

            data.add(MoneyItem("3000", 1, R.drawable.testimg1, i,1))
            data.add(MoneyItem("6000", 2, R.drawable.testimg2, i,1))
            data.add(MoneyItem("6000", 2, R.drawable.testimg1, i,1))
            data.add(MoneyItem("3000", 1, R.drawable.testimg2, i,1))
            data.add(MoneyItem("6000", 2, R.drawable.testimg1, i,1))
            data.add(MoneyItem("6000", 2, R.drawable.testimg2, i,1))

            data.add(MoneyItem("null", 0, 0, i,5))
            data.add(MoneyItem("null", 0, 0, i,2))
            data.add(MoneyItem("Total:1000", 0, 0, i,3))
        }
        adapter.notifyDataSetChanged()
    }

    fun eraseItem(position:Int, item:MoneyItem)
    {
        data.removeAt(position)
        var lastPos = 0;
        for(i in 0..data.size)
        {
            if(data.get(i).day == data.get(position).day && data.get(i).viewType == 5) {
                lastPos = i
                break;
            }
        }
        data.add(lastPos, MoneyItem("null", 0, 0, item.day,2))
        if(data.get(lastPos).viewType == 2 &&
            data.get(lastPos-1).viewType == 2 &&
            data.get(lastPos-2).viewType == 2)
        {
            data.removeAt(lastPos)
            data.removeAt(lastPos-1)
            data.removeAt(lastPos-2)
        }
        adapter.notifyDataSetChanged()
    }
    fun addItem(position:Int, price:String, cate:Int, img:Int, day:Int, viewType:Int)
    {
        var lastPos = 0;
        for(i in 0..data.size)
        {
            if(data.get(i).day == data.get(position).day && data.get(i).viewType == 5) {
                lastPos = i
                break;
            }
        }
        if(data.get(lastPos-1).viewType != 2) {
            data.add(lastPos, MoneyItem(price, cate, img, day, viewType))
            data.add(lastPos+1, MoneyItem("null", 0, 0, day, 2))
            data.add(lastPos+2, MoneyItem("null", 0, 0, day, 2))
        }
        else if(data.get(lastPos-2).viewType == 2)
        {
            data.removeAt(lastPos-2)
            data.add(lastPos-2, MoneyItem(price, cate, img, day, viewType))
        }
        else if(data.get(lastPos-1).viewType == 2)
        {
            data.removeAt(lastPos-1)
            data.add(lastPos-1, MoneyItem(price, cate, img, day, viewType))
        }
        adapter.notifyDataSetChanged()
    }
    fun ShowLayout(v: View?):Unit
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
        imageView.setImageResource(0)

    }

}



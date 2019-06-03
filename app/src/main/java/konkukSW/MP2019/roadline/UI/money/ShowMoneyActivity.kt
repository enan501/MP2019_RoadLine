package konkukSW.MP2019.roadline.UI.money

import android.app.Activity
import android.app.DownloadManager
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
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewManager
import android.widget.*
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmQuery
import konkukSW.MP2019.roadline.Data.DB.T_List
import io.realm.RealmResults
import io.realm.kotlin.delete
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import kotlinx.android.synthetic.main.detail_img.*
import java.util.*
import kotlin.collections.ArrayList


class ShowMoneyActivity : AppCompatActivity() {

    var data: ArrayList<MoneyItem> = ArrayList()
    lateinit var adapter: MoneyItemAdapter

    var ListID = ""
    var DayNum = 0;
    var dayCount = 0;

    var TotalPrice = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_money)

        val i = getIntent()
        ListID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum", 0)

        initLayout()
        initListener()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                initLayout() // 어댑터 갱신
                initListener()
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

    fun initListener() {
        /* 리사이클뷰 어댑터에 리스너 달기 */
        adapter.itemLongClickListener = object : MoneyItemAdapter.OnItemLongClickListener {
            override fun OnItemLongClick(
                holder: MoneyItemAdapter.ViewHolder1,
                view: View,
                item: MoneyItem,
                position: Int
            ) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val alert_confirm = AlertDialog.Builder(this@ShowMoneyActivity)
                alert_confirm.setMessage("삭제할래?").setCancelable(false).setPositiveButton("취소",
                    DialogInterface.OnClickListener { dialog, which ->
                        // content
                    }).setNegativeButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        eraseItem(item.dayNum, item)
                        return@OnClickListener
                    })
                val alert = alert_confirm.create()
                alert.show()
            }
        }
        adapter.itemClickListener = object : MoneyItemAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MoneyItemAdapter.ViewHolder4, view: View, item: MoneyItem, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                addItem(item.listID, item.DayNum,3000, 0, R.drawable.testimg1,
//                    "2019.05.20", 1)
                val intent = Intent(applicationContext, AddMoneyActivity::class.java)
                intent.putExtra("ListID", item.listID)
                intent.putExtra("DayNum", item.dayNum)
                startActivityForResult(intent, 123)
            }
        }
        adapter.itemClickListener2 = object : MoneyItemAdapter.OnItemClickListener2 {
            override fun OnItemClick2(
                holder: MoneyItemAdapter.ViewHolder1,
                view: View,
                item: MoneyItem,
                position: Int
            ) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                ShowLayout(item)
            }
        }

        detail_money.setOnClickListener {
            val intent = Intent(this, ShowDetailMoneyActivity::class.java)
            intent.putExtra("ListID", ListID)
            intent.putExtra("DayNum", DayNum)
            startActivityForResult(intent, 123)
        }
    }

    fun initLayout() {
        TotalPrice = 0;
        data.clear()
        val layoutManager = GridLayoutManager(this, 3)
        money_recycleView.layoutManager = layoutManager
        adapter = MoneyItemAdapter(data)
        money_recycleView.adapter = adapter

        Realm.init(this);
        val realm = Realm.getDefaultInstance()   // 현재 스레드에서 Realm의 인스턴스 가져오기
        if (DayNum == 0) // 리스트내 Day 전부 다 출력
        {
            val q = realm.where(T_Day::class.java)
                .equalTo("listID", ListID)
                .findAll()
            dayCount = q.size
            for (i in 1..dayCount) {
                val q2 = realm.where(T_Day::class.java)
                    .equalTo("listID", ListID)
                    .equalTo("num", i)
                    .findFirst()
                data.add(MoneyItem(ListID, i, UUID.randomUUID().toString(), -1, "", "", q2!!.date, 0))
                data.add(MoneyItem(ListID, i, UUID.randomUUID().toString(), -1, "", "", "NULL", 2))
                data.add(MoneyItem(ListID, i, UUID.randomUUID().toString(), -1, "", "", "NULL", 4))

                data.add(MoneyItem(ListID, i, UUID.randomUUID().toString(), -1, "", "", "NULL", 5))
                data.add(MoneyItem(ListID, i, UUID.randomUUID().toString(), -1, "", "", "NULL", 2))
                data.add(MoneyItem(ListID, i, UUID.randomUUID().toString(), 0, "", "", "NULL", 3))

                val q = realm.where(T_Money::class.java)
                    .equalTo("listID", ListID)
                    .equalTo("dayNum", i)
                    .findAll()

                for (i in 0..q.size - 1) {
                    System.out.println(q.get(i)!!.listID)
                    System.out.println(q.get(i)!!.dayNum)
                    System.out.println(q.get(i)!!.id)
                    System.out.println(q.get(i)!!.price)

                    addItem(
                        q.get(i)!!.listID, q.get(i)!!.dayNum, q.get(i)!!.id, q.get(i)!!.price,
                        q.get(i)!!.cate, q.get(i)!!.img, "2019.05.20", 1
                    )
                }
            }
        } else // 한개의 Day만 출력
        {
            val q2 = realm.where(T_Day::class.java)
                .equalTo("listID", ListID)
                .equalTo("num", DayNum)
                .findFirst()
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), -1, "", "", q2!!.date, 0))
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), -1, "", "", "NULL", 2))
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), -1, "", "", "NULL", 4))

            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), -1, "", "", "NULL", 5))
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), -1, "", "", "NULL", 2))
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), 0, "", "", "NULL", 3))

            val q = realm.where(T_Money::class.java)
                .equalTo("listID", ListID)
                .equalTo("dayNum", DayNum)
                .findAll()
            for (i in 0..q.size - 1) {
                System.out.println(q.get(i)!!.listID)
                System.out.println(q.get(i)!!.dayNum)
                System.out.println(q.get(i)!!.id)
                System.out.println(q.get(i)!!.price)
                System.out.println(q.get(i)!!.img)

                addItem(
                    q.get(i)!!.listID, q.get(i)!!.dayNum, q.get(i)!!.id, q.get(i)!!.price,
                    q.get(i)!!.cate, q.get(i)!!.img, "2019.05.20", 1
                )
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun eraseItem(position: Int, item: MoneyItem) {
        var removePrice = data.get(position).price
//        data.removeAt(position)
//        var lastPos = 0;
//        for(i in 0..data.size)
//        {
//            if(data.get(i).dayNum == data.get(position).dayNum && data.get(i).viewType == 5) {
//                lastPos = i
//                break;
//            }
//        }
//        data.add(lastPos, MoneyItem(item.listID, item.dayNum, UUID.randomUUID().toString(),-1, "", "", "NULL", 2))
//        if(data.get(lastPos).viewType == 2 &&
//            data.get(lastPos-1).viewType == 2 &&
//            data.get(lastPos-2).viewType == 2)
//        {
//            data.removeAt(lastPos)
//            data.removeAt(lastPos-1)
//            data.removeAt(lastPos-2)
//        }
        for (i in 0..data.size) // 토탈에 방금 제거한 가격 빼주기
        {
            if (data.get(i).dayNum == data.get(position).dayNum && data.get(i).viewType == 3) {
                data.get(i).price -= removePrice
                TotalPrice -= removePrice
                money_totalTextView.text = "Total " + TotalPrice.toString()
                break;
            }
        }

        Realm.init(this);
        val realm = Realm.getDefaultInstance()   // 현재 스레드에서 Realm의 인스턴스 가져오기

        val q = realm.where(T_Money::class.java)
            .equalTo("listID", item.listID)
            .equalTo("dayNum", item.dayNum)
            .equalTo("id", item.id)
            .findFirst()
        realm.beginTransaction()
        q!!.deleteFromRealm()
        realm.commitTransaction()

        data.clear()
        initLayout()
        initListener()
    }

    fun addItem(
        listID: String,
        DayNum: Int,
        id: String,
        price: Int,
        cate: String,
        img: String,
        date: String,
        viewType: Int
    ) {
        var lastPos = 0;
        for (i in 0..data.size) {
            if (data.get(i).dayNum == DayNum && data.get(i).viewType == 5) {
                lastPos = i
                break
            }
        }
        if (data.get(lastPos - 1).viewType != 2) {
            data.add(lastPos, MoneyItem(listID, DayNum, id, price, cate, img, date, viewType))
            data.add(lastPos + 1, MoneyItem(listID, DayNum, id, -1, "", "", "NULL", 2))
            data.add(lastPos + 2, MoneyItem(listID, DayNum, id, -1, "", "", "NULL", 2))
        } else if (data.get(lastPos - 2).viewType == 2) {
            data.removeAt(lastPos - 2)
            data.add(lastPos - 2, MoneyItem(listID, DayNum, id, price, cate, img, date, viewType))
        } else if (data.get(lastPos - 1).viewType == 2) {
            data.removeAt(lastPos - 1)
            data.add(lastPos - 1, MoneyItem(listID, DayNum, id, price, cate, img, date, viewType))
        }
        for (i in 0..data.size) // 토탈에 방금 추가한 가격 더해주기
        {
            if (data.get(i).dayNum == DayNum && data.get(i).viewType == 3) {
                data.get(i).price += price
                TotalPrice += price
                money_totalTextView.text = "Total " + TotalPrice.toString()
                break;
            }
        }
        adapter.notifyDataSetChanged()
    }

    fun ShowLayout(item: MoneyItem): Unit {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //레이아웃을 위에 겹쳐서 올리는 부분
        val ll = inflater.inflate(R.layout.detail_img, null) as LinearLayout //레이아웃 객체생성
        ll.setBackgroundColor(Color.parseColor("#99000000")) //레이아웃 배경 투명도 주기
        val paramll =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)
        addContentView(ll, paramll) //레이아웃 위에 겹치기
        ll.setOnClickListener {
            (ll.getParent() as ViewManager).removeView(ll)
        }
        var imageView = ll.findViewById<ImageView>(R.id.imageView) // 매번 새로운 레이어 이므로 ID를 find 해준다.
        var textView1 = ll.findViewById<TextView>(R.id.textView1)
        var textView2 = ll.findViewById<TextView>(R.id.textView2)
        textView1.text = item.price.toString()
        textView2.text = item.date.toString()
        if (item.img == "") {
            when (item.cate) {
                "식사" ->  imageView.setImageResource(R.drawable.meal)
                "쇼핑" ->  imageView.setImageResource(R.drawable.shopping)
                "교통" ->  imageView.setImageResource(R.drawable.transport)
                "관광" ->  imageView.setImageResource(R.drawable.tour)
                "숙박" ->  imageView.setImageResource(R.drawable.lodgment)
                "기타" ->  imageView.setImageResource(R.drawable.etc)
            }
        } else
            imageView.setImageBitmap(BitmapFactory.decodeFile(item.img))
    }
}



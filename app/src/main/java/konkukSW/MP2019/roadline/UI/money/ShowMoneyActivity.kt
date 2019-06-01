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
import android.graphics.Color
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
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import kotlinx.android.synthetic.main.detail_img.*


class ShowMoneyActivity : AppCompatActivity() {

    var data:ArrayList<MoneyItem> = ArrayList()
    lateinit var adapter:MoneyItemAdapter

    var ListID = ""; // 인텐트로 받아
    var DayNum = 0; // 인텐트로 받아
    var dayCount = 3; // 이것 나중에 디비에서 받아와야함

    var TotalPrice = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_money)
        initLayout()

        ListID = intent.getStringExtra("ListID");
        DayNum = intent.getIntExtra("DayNum", 0);

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
//                addItem(position, item.listID, item.DayNum,3000, 0, R.drawable.testimg1,
//                    "2019.05.20", 1)
                val intent = Intent(applicationContext, AddMoneyActivity::class.java)
                startActivityForResult(intent,123)

            }
        }
        adapter.itemClickListener2 = object : MoneyItemAdapter.OnItemClickListener2 {
            override fun OnItemClick2(holder: MoneyItemAdapter.ViewHolder1, view: View, item: MoneyItem, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                ShowLayout(item)
            }
        }

        detail_money.setOnClickListener {
            val intent = Intent(this, ShowDetailMoneyActivity::class.java)
            startActivity(intent)
        }

        Realm.init(this);
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build() // DB 테이블 수정시 자동으로 모든 인스턴스들 삭제모드
        Realm.setDefaultConfiguration(config) // 데이터베이스 옵션 설정해주는것 한번만 하면 됨.

//        Realm.init(this);
//        val realm = Realm.getDefaultInstance()   // 현재 스레드에서 Realm의 인스턴스 가져오기
////        val q = realm.where(T_List::class.java).findAll()
////        realm.beginTransaction()
////        val list: T_List = realm.createObject(T_List::class.java, q.size+1)//데이터베이스에 저장할 객체 생성
////        list.title = "ㅎㅇ"
////        list.date = "2011"
////        realm.commitTransaction()
//
//        val q3 = realm.where(T_Day::class.java).findAll()
//        for(i in 0..q3.size-1) {
//            System.out.println(q3.get(i)!!.listID.toString() + ", " + q3.get(i)!!.DayNum.toString())
//        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                //Toast.makeText(this, pass, Toast.LENGTH_SHORT).show()
                initLayout() // 어댑터 갱신
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
        data.clear()
        val layoutManager = GridLayoutManager(this, 3)
        money_recycleView.layoutManager = layoutManager
        adapter = MoneyItemAdapter(data)
        money_recycleView.adapter = adapter

        Realm.init(this);
        val realm = Realm.getDefaultInstance()   // 현재 스레드에서 Realm의 인스턴스 가져오기

        if(DayNum == 0) // 리스트내 Day 전부 다 출력
        {
            val q = realm.where(T_Money::class.java)
                .equalTo("listID", ListID)
                .findAll()

            for (i in 1..dayCount) {
                data.add(MoneyItem(ListID, i, 20190530, 0, 0, "NULL", 0))
                data.add(MoneyItem(ListID, i, -1, 0, 0, "NULL", 2))
                data.add(MoneyItem(ListID, i, -1, 0, 0, "NULL", 4))

                data.add(MoneyItem(ListID, i, -1, 0, 0, "NULL", 5))
                data.add(MoneyItem(ListID, i, -1, 0, 0, "NULL", 2))
                data.add(MoneyItem(ListID, i, 0, 0, 0, "NULL", 3))


            }
        }
        else
        {
            data.add(MoneyItem(ListID, DayNum, 20190530, 0, 0, "NULL", 0))
            data.add(MoneyItem(ListID, DayNum, -1, 0, 0, "NULL", 2))
            data.add(MoneyItem(ListID, DayNum, -1, 0, 0, "NULL", 4))

            data.add(MoneyItem(ListID, DayNum, -1, 0, 0, "NULL", 5))
            data.add(MoneyItem(ListID, DayNum, -1, 0, 0, "NULL", 2))
            data.add(MoneyItem(ListID, DayNum, 0, 0, 0, "NULL", 3))
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
        data.add(lastPos, MoneyItem(item.listID, item.dayNum,-1, 0, 0, "NULL", 2))
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
                TotalPrice -= removePrice
                money_totalTextView.text = "Total " + TotalPrice.toString()
                break;
            }
        }
        adapter.notifyDataSetChanged()
    }
    fun addItem(position:Int, listID:String, DayNum:Int, price:Int, cate:Int, img:Int, date:String, viewType:Int)
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
            data.add(lastPos, MoneyItem(listID, DayNum, price, cate, img, date, viewType))
            data.add(lastPos+1, MoneyItem(listID, DayNum,-1, 0, 0,"NULL",2))
            data.add(lastPos+2, MoneyItem(listID, DayNum,-1, 0, 0, "NULL", 2))
        }
        else if(data.get(lastPos-2).viewType == 2)
        {
            data.removeAt(lastPos-2)
            data.add(lastPos-2, MoneyItem(listID, DayNum, price, cate, img, date, viewType))
        }
        else if(data.get(lastPos-1).viewType == 2)
        {
            data.removeAt(lastPos-1)
            data.add(lastPos-1, MoneyItem(listID, DayNum, price, cate, img, date, viewType))
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
        var textView1 = ll.findViewById<TextView>(R.id.textView1)
        var textView2 = ll.findViewById<TextView>(R.id.textView2)
        textView1.text = item.price.toString()
        textView2.text = item.date.toString()
        imageView.setImageResource(item.img)
    }

}



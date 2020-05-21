package konkukSW.MP2019.roadline.UI.money

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewManager
import android.widget.*
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.MoneyItemAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_show_money.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class ShowMoneyActivity : AppCompatActivity() {

    var data: ArrayList<MoneyItem> = ArrayList()
    lateinit var MIadapter: MoneyItemAdapter
    var ListID = ""
    var DayNum = 0
    var dayCount = 0
    var rate = 0.0
    var symbol = "₩" // currency
    var code = "" // currency
    var TotalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_money)

        val i = getIntent()
        ListID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum", 0)

        initLayout()
        initListener()
        initCurrencyAdapter()
        currencySpinner.onItemSelectedListener = SpinnerSelectedListener()
    }

    inner class SpinnerSelectedListener : AdapterView.OnItemSelectedListener { // 오버라이딩 단축키 alt + enter
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Toast.makeText(parent?.context, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
            // 화폐고름

            val result = parent?.getItemAtPosition(position).toString()
            val c_codelist = result.split(" - ")
            val c_code = c_codelist[0]

            Realm.init(applicationContext)
            val realm = Realm.getDefaultInstance()
            // 해당 화페 가져옴
            val DB = realm.where(T_Currency::class.java)
                .equalTo("code", c_code)
                .findFirst()
            symbol = DB?.symbol.toString()
            code = DB?.code.toString()
            rate = DB?.rate.toString().toDouble()
            currencySymbol.text = " " + symbol
            //val exchange = TotalPrice / rate
            //money_totalTextView.text = exchange.toInt().toString()

            if (DayNum != 0) { // 화면에 보여지는 데이가 전체가 아님
                val c_day = realm.where(T_Day::class.java)
                    .equalTo("num", DayNum) // 현재 날짜를 골라낸다
                    .findFirst() // findfirst하면 한놈만 골라냄

                realm.beginTransaction()
                c_day?.currency = code //해당 날짜의 currency 변경
                realm.commitTransaction()
            } // 데이 전체면 변경 안함

            initLayout() // 어댑터 갱신
            initListener()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                initLayout() // 어댑터 갱신
                initListener()
                initCurrencyAdapter()
            }
        }
    }

    fun initSwipe() {
        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                MIadapter.moveItem(p1.adapterPosition, p2.adapterPosition)
                return true
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                MIadapter.removeItem(p0.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(money_recycleView)
    }

    fun initListener() {
        /* 리사이클뷰 어댑터에 리스너 달기 */
        MIadapter.itemLongClickListener = object : MoneyItemAdapter.OnItemLongClickListener {
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
                        eraseItem(position, item)
                        return@OnClickListener
                    })
                val alert = alert_confirm.create()
                alert.show()
            }
        }
        MIadapter.itemClickListener = object : MoneyItemAdapter.OnItemClickListener {
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
        MIadapter.itemClickListener2 = object : MoneyItemAdapter.OnItemClickListener2 {
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initLayout() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "가계부"

        TotalPrice = 0
        data.clear()

        Realm.init(this)
        val realm = Realm.getDefaultInstance()   // 현재 스레드에서 Realm의 인스턴스 가져오기
        if (DayNum == 0) // 리스트내 Day 전부 다 출력
        {
            val q = realm.where(T_Day::class.java)
                .equalTo("listID", ListID)
                .findAll()
            for (q2 in q) {
                data.add(MoneyItem(ListID, q2.num, UUID.randomUUID().toString(), 0, "", "", q2!!.date, 0, symbol))
                data.add(MoneyItem(ListID, q2.num, UUID.randomUUID().toString(), 0, "", "", "NULL", 2, symbol))
                data.add(MoneyItem(ListID, q2.num, UUID.randomUUID().toString(), 0, "", "", "NULL", 4, symbol))

                data.add(MoneyItem(ListID, q2.num, UUID.randomUUID().toString(), 0, "", "", "NULL", 5, symbol))
                data.add(MoneyItem(ListID, q2.num, UUID.randomUUID().toString(), 0, "", "", "NULL", 2, symbol))
                data.add(MoneyItem(ListID, q2.num, UUID.randomUUID().toString(), 0, "", "", "NULL", 3, symbol))

                val q = realm.where(T_Money::class.java)
                    .equalTo("listID", ListID)
                    .equalTo("dayNum", q2.num)
                    .findAll()

                for (q3 in q) {
//                    System.out.println(q.get(i)!!.listID)
//                    System.out.println(q.get(i)!!.dayNum)
//                    System.out.println(q.get(i)!!.id)
//                    System.out.println((q.get(i)!!.price / rate).roundToInt())

                    addItem(
                        q3.listID, q3.dayNum, q3.id, (q3.price / rate).roundToInt(),
                        q3.cate, q3.img, q3.date, 1
                    )
                }
            }
        } else // 한개의 Day만 출력
        {
            val q2 = realm.where(T_Day::class.java)
                .equalTo("listID", ListID)
                .equalTo("num", DayNum)
                .findFirst()
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), 0, "", "", q2!!.date, 0, symbol))
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), 0, "", "", "NULL", 2, symbol))
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), 0, "", "", "NULL", 4, symbol))

            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), 0, "", "", "NULL", 5, symbol))
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), 0, "", "", "NULL", 2, symbol))
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), 0, "", "", "NULL", 3, symbol))

            val q = realm.where(T_Money::class.java)
                .equalTo("listID", ListID)
                .equalTo("dayNum", DayNum)
                .findAll()
            for (q3 in q) {
//                System.out.println(q.get(i)!!.listID)
//                System.out.println(q.get(i)!!.dayNum)
//                System.out.println(q.get(i)!!.id)
//                System.out.println((q.get(i)!!.price / rate).roundToInt())
//                System.out.println(q.get(i)!!.img)

                addItem(
                    q3.listID, q3.dayNum, q3.id, (q3.price / rate).roundToInt(),
                    q3.cate, q3.img, q3.date, 1
                )
            }
        }

        val layoutManager = GridLayoutManager(this, 3)
        money_recycleView.layoutManager = layoutManager
        MIadapter = MoneyItemAdapter(data)
        money_recycleView.adapter = MIadapter
        MIadapter.notifyDataSetChanged()
    }

    fun initCurrencyAdapter() {
        val Cadapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            ArrayList<String>()
        )

        Realm.init(this)
        val realm = Realm.getDefaultInstance()
        val DBlist = realm.where(T_Currency::class.java).findAll()
        for (T_currency in DBlist) {
            //if(currencyitem)
            Cadapter.add(T_currency.code + " - " + T_currency.name)
            println(T_currency.name + " : " + T_currency.code + " : " + T_currency.symbol + " : " + T_currency.rate)
        }
        currencySpinner.adapter = Cadapter
        // 해당 화페 가져옴

        val find = realm.where(T_Currency::class.java).findAll()
        val d_currency = realm.where(T_Day::class.java).equalTo("num", DayNum).findFirst()

        println(d_currency?.currency)
        if (DayNum != 0) { // 데이가 전체가 아니면 데이에 따라 스피너 선택되어있음
            // 처음 아니면 계속 이전것
            if (d_currency?.currency == "KRW") { // 통화를 따로 선택하지 않았다
                for (i in 0..find.size - 1)
                    if (find.get(i)!!.code == "KRW")   // 현재 코드로 변경함
                        currencySpinner.setSelection(i)
            } else { // 통화를 따로 선택하지 않았다
                for (i in 0..find.size - 1)
                    if (find.get(i)!!.code == d_currency?.currency)   // 현재 코드로 변경함
                        currencySpinner.setSelection(i)
            }
        } else { // 데이 전체면 그냥 KRW로 스피너 선택
            for (i in 0..find.size - 1)
                if (find.get(i)!!.code == "KRW")  // 현재 코드로 변경함
                    currencySpinner.setSelection(i)

        }
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
            data.add(lastPos, MoneyItem(listID, DayNum, id, price, cate, img, date, viewType, symbol))
            data.add(lastPos + 1, MoneyItem(listID, DayNum, id, 0, "", "", "NULL", 2, symbol))
            data.add(lastPos + 2, MoneyItem(listID, DayNum, id, 0, "", "", "NULL", 2, symbol))
        } else if (data.get(lastPos - 2).viewType == 2) {
            data.removeAt(lastPos - 2)
            data.add(lastPos - 2, MoneyItem(listID, DayNum, id, price, cate, img, date, viewType, symbol))
        } else if (data.get(lastPos - 1).viewType == 2) {
            data.removeAt(lastPos - 1)
            data.add(lastPos - 1, MoneyItem(listID, DayNum, id, price, cate, img, date, viewType, symbol))
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

    }

    fun ShowLayout(item: MoneyItem): Unit {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //레이아웃을 위에 겹쳐서 올리는 부분
        val ll = inflater.inflate(R.layout.detail_money_img, null) as LinearLayout //레이아웃 객체생성
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
        textView1.text = item.price.toString() + " " + item.symbol
        textView2.text = item.date.toString()
        if (item.img == "") {
            when (item.cate) {
                "식사" -> imageView.setImageResource(R.drawable.meal_big)
                "쇼핑" -> imageView.setImageResource(R.drawable.shopping_big)
                "교통" -> imageView.setImageResource(R.drawable.transport_big)
                "관광" -> imageView.setImageResource(R.drawable.tour_big)
                "숙박" -> imageView.setImageResource(R.drawable.lodgment_big)
                "기타" -> imageView.setImageResource(R.drawable.etc_big)
            }
        } else
            imageView.setImageBitmap(BitmapFactory.decodeFile(item.img))
    }
}



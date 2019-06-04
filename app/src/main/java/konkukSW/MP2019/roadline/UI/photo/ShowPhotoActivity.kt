package konkukSW.MP2019.roadline.UI.photo

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewManager
import android.widget.*
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.MoneyItemAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.DB.T_Photo
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.money.AddMoneyActivity
import konkukSW.MP2019.roadline.UI.money.ShowDetailMoneyActivity
import kotlinx.android.synthetic.main.activity_show_money.*
import kotlinx.android.synthetic.main.activity_show_photo.*
import java.util.*
import kotlin.collections.ArrayList

class ShowPhotoActivity : AppCompatActivity() {

    var data: ArrayList<MoneyItem> = ArrayList()
    lateinit var MIadapter: MoneyItemAdapter
    var ListID = ""
    var DayNum = 0
    var dayCount = 0
    var rate = 0.0
    var symbol = "" // currency
    var code = "" // currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_photo)

        val i = getIntent()
        ListID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum", 0)

        initLayout()
        initListener()
    }

    inner class SpinnerSelectedListener : AdapterView.OnItemSelectedListener { // 오버라이딩 단축키 alt + enter
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Toast.makeText(parent?.context, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
            // 화폐고름
        }
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
                val alert_confirm = AlertDialog.Builder(this@ShowPhotoActivity)
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
        MIadapter.itemClickListener = object : MoneyItemAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MoneyItemAdapter.ViewHolder4, view: View, item: MoneyItem, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                addItem(item.listID, item.DayNum,3000, 0, R.drawable.testimg1,
//                    "2019.05.20", 1)
                val intent = Intent(applicationContext, AddPhotoActivity::class.java)
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
    }

    fun initLayout() {
        data.clear()
        val layoutManager = GridLayoutManager(this, 3)
        photo_recycleView.layoutManager = layoutManager
        MIadapter = MoneyItemAdapter(data)
        photo_recycleView.adapter = MIadapter

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
                data.add(MoneyItem(ListID, i, UUID.randomUUID().toString(), 0, "", "", "NULL", 2))

                val q = realm.where(T_Photo::class.java)
                    .equalTo("listID", ListID)
                    .equalTo("dayNum", i)
                    .findAll()

                for (i in 0..q.size - 1) {
                    addItem(q.get(i)!!.listID, q.get(i)!!.dayNum, q.get(i)!!.id, q.get(i)!!.img, q.get(i)!!.date, 1)
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
            data.add(MoneyItem(ListID, DayNum, UUID.randomUUID().toString(), 0, "", "", "NULL", 2))

            val q = realm.where(T_Photo::class.java)
                .equalTo("listID", ListID)
                .equalTo("dayNum", DayNum)
                .findAll()
            for (i in 0..q.size - 1) {
                addItem(q.get(i)!!.listID, q.get(i)!!.dayNum, q.get(i)!!.id, q.get(i)!!.img, q.get(i)!!.date, 1)
            }
        }
        MIadapter.notifyDataSetChanged()
    }

    fun eraseItem(position: Int, item: MoneyItem) {
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

        Realm.init(this);
        val realm = Realm.getDefaultInstance()   // 현재 스레드에서 Realm의 인스턴스 가져오기

        val q = realm.where(T_Photo::class.java)
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
            data.add(lastPos, MoneyItem(listID, DayNum, id, -5, "", img, date, viewType))
            data.add(lastPos + 1, MoneyItem(listID, DayNum, id, -1, "", "", "NULL", 2))
            data.add(lastPos + 2, MoneyItem(listID, DayNum, id, -1, "", "", "NULL", 2))
        } else if (data.get(lastPos - 2).viewType == 2) {
            data.removeAt(lastPos - 2)
            data.add(lastPos - 2, MoneyItem(listID, DayNum, id, -5, "", img, date, viewType))
        } else if (data.get(lastPos - 1).viewType == 2) {
            data.removeAt(lastPos - 1)
            data.add(lastPos - 1, MoneyItem(listID, DayNum, id, -5, "", img, date, viewType))
        }
        MIadapter.notifyDataSetChanged()
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
        var textView2 = ll.findViewById<TextView>(R.id.textView2)
        var textView1 = ll.findViewById<TextView>(R.id.textView1)
        textView2.text = item.date.toString()
        textView1.text = ""
        if (item.img == "") {
                imageView.setImageResource(R.drawable.logo)
        } else
            imageView.setImageBitmap(BitmapFactory.decodeFile(item.img))
    }
}



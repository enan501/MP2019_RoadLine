package konkukSW.MP2019.roadline.UI.money

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.*
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.Adapter.MoneyListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_add_money.*
import kotlinx.android.synthetic.main.activity_show_money.*
import org.w3c.dom.Text
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class ShowMoneyActivity : AppCompatActivity() {

    var data: ArrayList<MoneyItem> = ArrayList()
    lateinit var rViewAdapter: MoneyListAdapter
    var realm = Realm.getDefaultInstance()
    var ListID = ""
    var DayNum = 0
    var isAll = false
    var dayList:ArrayList<T_Day> = ArrayList()
    lateinit var selectedCurrency:T_Currency
    var totalMoney = 0.0
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_money)
        init()
    }

    fun init(){
        Realm.init(this)
        initData()
        initCurrencyAdapter()
        initLayout()
        initListener()
    }

    fun initData(){
        val i = getIntent()
        ListID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum", 0)
        if(DayNum == 0){ //모든 날짜
            val dayItems = realm.where(T_Day::class.java).equalTo("listID", ListID).findAll()!!.sort("num")
            dayList.addAll(dayItems)
            isAll = true
        }
        else{
            val dayItem = realm.where(T_Day::class.java).equalTo("listID", ListID).equalTo("num", DayNum).findFirst()!!
            dayList.add(dayItem)
            isAll = false
        }

        for(i in dayList){
            for(j in i.moneyList){
                totalMoney += j.price
            }
        }
        rViewAdapter = MoneyListAdapter(this@ShowMoneyActivity, dayList, isAll)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if(resultCode == Activity.RESULT_OK){
                val pos = data!!.getIntExtra("pos", -1)
                val price = data!!.getDoubleExtra("price", 0.0)
                rViewAdapter.notifyItemChanged(pos)
                totalMoney += price
                val num = totalMoney * (1 / selectedCurrency.rate)
                inputTotalTextView(num)
            }
        }
    }

    fun initListener() {
        rViewAdapter.itemClickListener = object :MoneyListAdapter.OnItemClickListener{
            override fun onButtonClick(holder: MoneyListAdapter.ViewHolder, view: View, data: T_Day, position: Int) {
                val intent = Intent(this@ShowMoneyActivity, AddMoneyActivity::class.java)
                intent.putExtra("pos", position)
                intent.putExtra("ListID", data.listID)
                intent.putExtra("DayNum", data.num)
                intent.putExtra("cur", selectedCurrency.code)
                startActivityForResult(intent, 123)
            }

            override fun onItemClick(data: T_Money) {
                showImage(data)
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

        money_recycleView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        money_recycleView.adapter = rViewAdapter
        val animator = money_recycleView.itemAnimator
        if(animator is SimpleItemAnimator){
            animator.supportsChangeAnimations = false
        }
        val num = totalMoney * (1 / selectedCurrency.rate)
        inputTotalTextView(num)
    }

    fun initCurrencyAdapter() {
        val thisList = realm.where(T_List::class.java).equalTo("id", ListID).findFirst()!!
        selectedCurrency = thisList.currencys[0]!!
        val cAdapter = object :ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                var convertView = convertView
                if (convertView == null) {
                    convertView = LayoutInflater.from(this@ShowMoneyActivity).inflate(android.R.layout.simple_spinner_item, null)
                }
                (convertView as TextView).text = thisList.currencys[position]!!.symbol
                convertView.setTextColor(ContextCompat.getColor(this@ShowMoneyActivity, R.color.darkGray))
                convertView.textSize = 25f
                convertView.gravity = View.TEXT_ALIGNMENT_CENTER
                return convertView
            }
        }
        for (T_currency in thisList.currencys) {
            cAdapter.add(T_currency.code + " - " + T_currency.name)
        }
        currencySpinner.adapter = cAdapter
        currencySpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Toast.makeText(parent?.context, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
                val result = parent!!.getItemAtPosition(position).toString()
                val selectedCode = result.split(" - ")[0].trim()
                selectedCurrency = realm.where(T_Currency::class.java).equalTo("code", selectedCode).findFirst()!!
                rViewAdapter.notifyDataSetChanged()
                val num = totalMoney * (1 / selectedCurrency.rate)
                inputTotalTextView(num)
            }
        }
    }

    fun showImage(item: T_Money){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //레이아웃을 위에 겹쳐서 올리는 부분
        val ll = inflater.inflate(R.layout.detail_money_img, null) as LinearLayout //레이아웃 객체생성
        ll.setBackgroundColor(Color.parseColor("#99000000")) //레이아웃 배경 투명도 주기
        val paramll = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT)
        addContentView(ll, paramll) //레이아웃 위에 겹치기
        ll.setOnClickListener {
            (ll.getParent() as ViewManager).removeView(ll)
        }
        var imageView = ll.findViewById<ImageView>(R.id.priceImage) // 매번 새로운 레이어 이므로 ID를 find 해준다.
        var textView1 = ll.findViewById<TextView>(R.id.textView1)
        var textView2 = ll.findViewById<TextView>(R.id.textView2)
        textView1.text = item.price.toString() + " " + item.currency!!.symbol
        val num = item.price * (1 / item.currency!!.rate)
        if(num.toString().length >= 6){
            textView1.text = shortFormat.format(num) + " " + item.currency!!.symbol
        }
        else{
            textView1.text =longFormat.format(num) + " " + item.currency!!.symbol
        }
        textView2.text = item.date
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

    fun inputTotalTextView(num:Double){
        if(num.roundToInt().toString().length >= 6){
            money_totalTextView.text = "Total " +  shortFormat.format(num)
        }
        else{
            money_totalTextView.text = "Total " + longFormat.format(num)
        }
    }
}



package konkukSW.MP2019.roadline.UI.money

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.MoneyPhotoListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_show_money.*
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.roundToInt


class ShowMoneyActivity : AppCompatActivity() {

    lateinit var rViewAdapterPhoto: MoneyPhotoListAdapter
    lateinit var realm :Realm
    var ListID = ""
    var DayNum = 0
    var isAll = false
    lateinit var dayList:RealmResults<T_Day>
    lateinit var moneyResults: RealmResults<T_Money>
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
        realm = Realm.getDefaultInstance()
        val i = intent
        ListID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum", 0)
        if(DayNum == 0){ //모든 날짜
            dayList = realm.where(T_Day::class.java).equalTo("listID", ListID).findAll()!!.sort("num")
            moneyResults = realm.where(T_Money::class.java).equalTo("listID", ListID).findAll()!!
            isAll = true
        }
        else{
            dayList =  realm.where(T_Day::class.java).equalTo("listID", ListID).equalTo("num", DayNum).findAll()!!
            moneyResults = realm.where(T_Money::class.java).equalTo("listID", ListID).equalTo("dayNum", DayNum).findAll()!!
            isAll = false
        }
        for(j in moneyResults){
            totalMoney += (j.price * j.currency!!.rate)
        }
        rViewAdapterPhoto = MoneyPhotoListAdapter(dayList, this@ShowMoneyActivity, isAll, true)

    }

    fun initListener() {
        rViewAdapterPhoto.moneyItemClickListener = object :MoneyPhotoListAdapter.OnMoneyItemClickListener{
            override fun onButtonClick(holder: MoneyPhotoListAdapter.ViewHolder, view: View, data: T_Day, position: Int) {
                val intent = Intent(this@ShowMoneyActivity, AddMoneyActivity::class.java)
                intent.putExtra("pos", position)
                intent.putExtra("ListID", data.listID)
                intent.putExtra("DayNum", data.num)
                intent.putExtra("cur", selectedCurrency.code)
                startActivity(intent)
            }

            override fun onMoneyItemClick(data: T_Money) {
                showImage(data)
            }
        }
        rViewAdapterPhoto.totalViewChangeListener = object :MoneyPhotoListAdapter.OnTotalViewChangeListener{
            override fun onTotalViewChange(position: Int) {
                changeTotalView(position)
            }
        }

        detail_money.setOnClickListener {
            val intent = Intent(this, ShowDetailMoneyActivity::class.java)
            intent.putExtra("ListID", ListID)
            intent.putExtra("DayNum", DayNum)
            startActivityForResult(intent, 123)
        }
        moneyResults.addChangeListener { _ ->
            totalMoney = 0.0
            for(j in moneyResults){
                totalMoney += (j.price * j.currency!!.rate)
            }
            inputTotalTextView(totalMoney / selectedCurrency.rate)
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

        money_recycleView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        money_recycleView.adapter = rViewAdapterPhoto
        val animator = money_recycleView.itemAnimator
        if(animator is SimpleItemAnimator){
            animator.supportsChangeAnimations = false
        }
        val num = totalMoney / selectedCurrency.rate
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
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val result = parent!!.getItemAtPosition(position).toString()
                val selectedCode = result.split(" - ")[0].trim()
                selectedCurrency = realm.where(T_Currency::class.java).equalTo("code", selectedCode).findFirst()!!
                val num = totalMoney * (1 / selectedCurrency.rate)
                inputTotalTextView(num)

                if(isAll){
                    for(i in 0 until rViewAdapterPhoto.itemCount){
                        changeTotalView(i)
                    }
                }
            }
        }
    }

    fun changeTotalView(pos:Int){
        val results = moneyResults.where().equalTo("dayNum", pos + 1).findAll()!!
        var total = 0.0
        for(j in results){
            total += j.price * j.currency!!.rate
        }
        if(total.roundToInt().toString().length >= 6 || selectedCurrency.code == "KRW"){
            (money_recycleView.findViewHolderForAdapterPosition(pos) as MoneyPhotoListAdapter.ViewHolder).totalText.text = shortFormat.format(total / selectedCurrency.rate) + selectedCurrency.symbol
        }
        else{
            (money_recycleView.findViewHolderForAdapterPosition(pos) as MoneyPhotoListAdapter.ViewHolder).totalText.text = longFormat.format(total / selectedCurrency.rate) + selectedCurrency.symbol
        }
    }

    fun showImage(item: T_Money){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //레이아웃을 위에 겹쳐서 올리는 부분
        val layout = inflater.inflate(R.layout.detail_money_img, null) as ConstraintLayout //레이아웃 객체생성
        layout.setBackgroundColor(Color.parseColor("#99000000")) //레이아웃 배경 투명도 주기
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        addContentView(layout, lp) //레이아웃 위에 겹치기
        layout.setOnClickListener {
            (layout.parent as ViewManager).removeView(layout)
        }
        var imageView = layout.findViewById<ImageView>(R.id.priceImage) // 매번 새로운 레이어 이므로 ID를 find 해준다.
        var textView1 = layout.findViewById<TextView>(R.id.textView1)
        var textView2 = layout.findViewById<TextView>(R.id.textView2)
        val num = item.price
        if(num.roundToInt().toString().length >= 6){
            textView1.text = shortFormat.format(num) + " " + item.currency!!.symbol
        }
        else{
            textView1.text =longFormat.format(num) + " " + item.currency!!.symbol
        }
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            val dateFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = LocalDateTime.ofEpochSecond(item!!.dateTime, 0, ZoneOffset.of("+09:00"))
            textView2.text = dateTime.format(dateFormat)
        }
        else{
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = org.threeten.bp.LocalDateTime.ofEpochSecond(item!!.dateTime, 0, org.threeten.bp.ZoneOffset.of("+09:00"))
            textView2.text = dateTime.format(dateFormat)
        }
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
        if(num.roundToInt().toString().length >= 6 || selectedCurrency.code == "KRW"){
            money_totalTextView.text = "Total " +  shortFormat.format(num)
        }
        else{
            money_totalTextView.text = "Total " + longFormat.format(num)
        }
    }
}



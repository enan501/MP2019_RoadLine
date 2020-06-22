package konkukSW.MP2019.roadline.UI.money

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.MoneyGridAdapter
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
    var deleteMode = false
    lateinit var dayList:RealmResults<T_Day>
    lateinit var moneyResults: RealmResults<T_Money>
    lateinit var selectedCurrency:T_Currency
    var totalMoney = 0.0
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")
    var deleteMoneyList: ArrayList<T_Money> = ArrayList()


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

    fun changeViewToDeleteMode(flg: Boolean){
        for(i in 0 until rViewAdapterPhoto.itemCount){
            val viewHolder = money_recycleView.findViewHolderForAdapterPosition(i)
            if(viewHolder != null){
                for(j in 0 until (viewHolder as MoneyPhotoListAdapter.ViewHolder).rView.childCount){
                    val itemViewHolder = viewHolder.rView.findViewHolderForAdapterPosition(j)
                    if(itemViewHolder != null){
                        if(flg){
                            (itemViewHolder as MoneyGridAdapter.ViewHolder).isChecked = false
                            itemViewHolder.checkButton.visibility = View.INVISIBLE
                        }
                        else{
                            (itemViewHolder as MoneyGridAdapter.ViewHolder).isChecked = false
                            itemViewHolder.checkButton.visibility = View.VISIBLE
                            itemViewHolder.checkButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                        }

                    }
                }
            }
        }
    }

    fun initListener() {
        deleteButton.setOnClickListener {
            if(deleteMode){
                if(deleteMoneyList.isNotEmpty()){
                    val builder = AlertDialog.Builder(this@ShowMoneyActivity)
                    builder.setMessage("삭제하시겠습니까?")
                            .setPositiveButton("삭제") { dialogInterface, _ ->
                                Log.d("mytag", deleteMoneyList.toString())
                                realm.beginTransaction()
                                for(i in deleteMoneyList){
                                    i.deleteFromRealm()
                                }
                                realm.commitTransaction()
                                changeViewToDeleteMode(deleteMode)
                                deleteMoneyList.clear()
                                deleteText.text = "수정하기"
                                deleteMode = false
                            }
                            .setNegativeButton("취소") { dialogInterface, i ->
                                changeViewToDeleteMode(deleteMode)
                                deleteMoneyList.clear()
                                deleteText.text = "수정하기"
                                deleteMode = false
                            }
                    val dialog = builder.create()
                    dialog.show()
                }
                else{
                    deleteText.text = "수정하기"
                    deleteMode = false
                }
            }
            else{
                deleteText.text = "삭제하기"
                changeViewToDeleteMode(deleteMode)
                deleteMode = true
            }
        }
        rViewAdapterPhoto.moneyItemClickListener = object :MoneyPhotoListAdapter.OnMoneyItemClickListener{
            override fun onButtonClick(holder: MoneyPhotoListAdapter.ViewHolder, view: View, data: T_Day, position: Int) { //추가
                val intent = Intent(this@ShowMoneyActivity, AddMoneyActivity::class.java)
                intent.putExtra("ListID", data.listID)
                intent.putExtra("DayNum", data.num)
                intent.putExtra("cur", selectedCurrency.code)
                intent.putExtra("editMode", false)
                startActivity(intent)
            }

            override fun onMoneyItemClick(holder: MoneyGridAdapter.ViewHolder, view: View, data: T_Money, position: Int, isChecked: Boolean) { //수정
                if(deleteMode){
                    if(isChecked){
                        holder.checkButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                        deleteMoneyList.remove(data)
                        Log.d("mytag", "remove : " + deleteMoneyList.toString())
                    }
                    else{
                        holder.checkButton.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                        deleteMoneyList.add(data)
                        Log.d("mytag", "add : " +deleteMoneyList.toString())
                    }
                }
                else{
                    val intent = Intent(this@ShowMoneyActivity, AddMoneyActivity::class.java)
                    intent.putExtra("ListID", data.listID)
                    intent.putExtra("DayNum", data.dayNum)
                    intent.putExtra("moneyId", data.id)
                    intent.putExtra("editMode", true)
                    startActivity(intent)
                }
            }
        }
        rViewAdapterPhoto.totalViewChangeListener = object :MoneyPhotoListAdapter.OnTotalViewChangeListener{
            override fun onTotalViewChange(position: Int) {
                changeTotalView(position)
            }
        }

        detail_money.setOnClickListener {
            if(moneyResults.isEmpty()){
                val builder = AlertDialog.Builder(this@ShowMoneyActivity)
                builder.setMessage("가계부에 내용을 추가해주세요")
                        .setPositiveButton("확인") { dialogInterface, _ ->

                        }
                val dialog = builder.create()
                dialog.show()
            }
            else{
                val intent = Intent(this, ShowDetailMoneyActivity::class.java)
                intent.putExtra("ListID", ListID)
                intent.putExtra("DayNum", DayNum)
                startActivityForResult(intent, 123)
            }
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
        val viewHoler = money_recycleView.findViewHolderForAdapterPosition(pos)
        if(viewHoler != null){
            if(total.roundToInt().toString().length >= 6 || selectedCurrency.code == "KRW"){
                (viewHoler as MoneyPhotoListAdapter.ViewHolder).totalText.text = shortFormat.format(total / selectedCurrency.rate) + selectedCurrency.symbol
            }
            else{
                (viewHoler as MoneyPhotoListAdapter.ViewHolder).totalText.text = longFormat.format(total / selectedCurrency.rate) + selectedCurrency.symbol
            }
        }
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



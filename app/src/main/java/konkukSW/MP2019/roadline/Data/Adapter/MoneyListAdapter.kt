package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.R
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MoneyListAdapter(realmResult:OrderedRealmCollection<T_Day>, val context:Context, val isAll: Boolean) : RealmRecyclerViewAdapter<T_Day, MoneyListAdapter.ViewHolder>(realmResult, true) {


    interface OnItemClickListener {
        fun onButtonClick(holder: ViewHolder, view: View, data: T_Day, position: Int)
        fun onItemClick(data: T_Money)
    }

    interface OnTotalViewChangeListener{
        fun onTotalViewChange(position: Int)
    }

    init {
        Realm.init(context)
    }

    lateinit var itemClickListener: OnItemClickListener
    lateinit var totalViewChangeListener: OnTotalViewChangeListener
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")
    val realm = Realm.getDefaultInstance()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dayNumText:TextView
        var dateText:TextView
        var addButton:ImageView
        var rView: RecyclerView
        var totalText:TextView

        init {
            dayNumText = itemView.findViewById(R.id.dayNumText)
            dateText = itemView.findViewById(R.id.dateText)
            addButton = itemView.findViewById(R.id.addButton)
            rView = itemView.findViewById(R.id.rView)
            totalText = itemView.findViewById(R.id.totalText)
            addButton.setOnClickListener {
                itemClickListener.onButtonClick(this, it, getItem(adapterPosition)!!, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_money_day, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if(p0 is ViewHolder){
            val item = getItem(p1)!!
            p0.dayNumText.text = "DAY" + item.num.toString()
            p0.dateText.text = item.date
            val result = realm.where(T_Money::class.java).equalTo("listID", item.listID).equalTo("dayNum", item.num).findAll()!!.sort("date")
            result.addChangeListener{ _ ->
                totalViewChangeListener.onTotalViewChange(p1)
            }
            val moneyAdapter = MoneyGridAdapter(result, context)
            moneyAdapter.itemClickListener = object :MoneyGridAdapter.OnItemClickListener{
                override fun onItemClick(
                        holder: MoneyGridAdapter.ViewHolder, view: View, data: T_Money, position: Int) {
                    itemClickListener.onItemClick(data)
                }
            }
            p0.rView.adapter = moneyAdapter
            val animator = p0.rView.itemAnimator
            if(animator is SimpleItemAnimator){
                animator.supportsChangeAnimations = false
            }
            p0.rView.layoutManager = GridLayoutManager(context, 3)
            var totalKorPrice = 0.0
            for(i in result){
                totalKorPrice += i.price * i.currency!!.rate
            }
            if(isAll){
                p0.totalText.text = shortFormat.format(totalKorPrice) + "₩"
            }
            else{
                p0.totalText.visibility = View.GONE
            }
        }
    }
}
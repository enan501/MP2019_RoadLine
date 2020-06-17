package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.Data.DB.T_Photo
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.photo.DetailPhotoActivity
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MoneyPhotoListAdapter(realmResult:OrderedRealmCollection<T_Day>, val context:Context, val isAll: Boolean, val isMoneyType: Boolean) : RealmRecyclerViewAdapter<T_Day, MoneyPhotoListAdapter.ViewHolder>(realmResult, true) {


    interface OnMoneyItemClickListener {
        fun onButtonClick(holder: ViewHolder, view: View, data: T_Day, position: Int)
        fun onMoneyItemClick(data: T_Money)
    }

    interface OnPhotoItemClickListener {
        fun onButtonClick(holder: ViewHolder, view: View, data: T_Day, position: Int)
    }

    interface OnTotalViewChangeListener{
        fun onTotalViewChange(position: Int)
    }

    var photoItemClickListener: OnPhotoItemClickListener? = null
    var moneyItemClickListener: OnMoneyItemClickListener? = null
    var totalViewChangeListener: OnTotalViewChangeListener? = null
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")
    var realm:Realm

    init {
        Realm.init(context)
        realm = Realm.getDefaultInstance()
    }

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
                if(isMoneyType)
                    moneyItemClickListener!!.onButtonClick(this, it, getItem(adapterPosition)!!, adapterPosition)
                else
                    photoItemClickListener!!.onButtonClick(this, it, getItem(adapterPosition)!!, adapterPosition)
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
            if(android.os.Build.VERSION.SDK_INT >= 26) {
                val dateFormat = DateTimeFormatter.ofPattern("M월 dd일 E요일").withLocale(Locale.forLanguageTag("ko"))
                val date = LocalDate.ofEpochDay(item.date)
                p0.dateText.text = date.format(dateFormat)
            }
            else{
                val dateFormat = org.threeten.bp.format.DateTimeFormatter.ofPattern("M월 dd일 E요일").withLocale(Locale.forLanguageTag("ko"))
                val date = org.threeten.bp.LocalDate.ofEpochDay(item.date)
                p0.dateText.text = date.format(dateFormat)
            }

            if(isMoneyType){
                val result = realm.where(T_Money::class.java).equalTo("listID", item.listID).equalTo("dayNum", item.num).findAll()!!.sort("dateTime")
                result.addChangeListener{ _ ->
                    totalViewChangeListener!!.onTotalViewChange(p1)
                }
                val moneyAdapter = MoneyGridAdapter(result, context)
                moneyAdapter.itemClickListener = object :MoneyGridAdapter.OnItemClickListener{
                    override fun onItemClick(
                            holder: MoneyGridAdapter.ViewHolder, view: View, data: T_Money, position: Int) {
                        moneyItemClickListener!!.onMoneyItemClick(data)
                    }
                }
                p0.rView.adapter = moneyAdapter
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
            else{
                val result = realm.where(T_Photo::class.java).equalTo("listID", item.listID).equalTo("dayNum", item.num).findAll()!!.sort("dateTime")
//                photoArray.add(result)
                val photoAdapter = PhotoGridAdapter(result, context)
                photoAdapter.itemClickListener = object :PhotoGridAdapter.OnItemClickListener{
                    override fun onItemClick(
                            holder: PhotoGridAdapter.ViewHolder,
                            view: View,
                            data: T_Photo,
                            position: Int
                    ) {
                        //intent
                        val intent = Intent(view.context, DetailPhotoActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("isAll", isAll)
                        intent.putExtra("photoId", data.id)
                        intent.putExtra("listId", item.listID)
                        intent.putExtra("dayNum", item.num)
                        view.context.startActivity(intent)
                    }
                }
                p0.rView.adapter = photoAdapter
                p0.totalText.visibility = View.GONE
            }
            val animator = p0.rView.itemAnimator
            if(animator is SimpleItemAnimator){
                animator.supportsChangeAnimations = false
            p0.rView.layoutManager = GridLayoutManager(context, 3)
            }
        }
    }
}
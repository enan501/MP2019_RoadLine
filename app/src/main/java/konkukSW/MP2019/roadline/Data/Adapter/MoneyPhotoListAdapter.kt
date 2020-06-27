package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
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
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import konkukSW.MP2019.roadline.UI.photo.DetailPhotoActivity
import konkukSW.MP2019.roadline.UI.photo.ShowPhotoActivity
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MoneyPhotoListAdapter(realmResult:OrderedRealmCollection<T_Day>, val context:Context, val isAll: Boolean, val isMoneyType: Boolean) : RealmRecyclerViewAdapter<T_Day, MoneyPhotoListAdapter.ViewHolder>(realmResult, true) {


    interface OnMoneyItemClickListener {
        fun onButtonClick(holder: ViewHolder, view: View, data: T_Day?, position: Int)
        fun onMoneyItemClick(holder: MoneyGridAdapter.ViewHolder, view: View, data: T_Money, position: Int, isChecked: Boolean)
    }

    interface OnPhotoItemClickListener {
        fun onButtonClick(holder: ViewHolder, view: View, data: T_Day, position: Int)
        fun onPhotoItemClick(holder: PhotoGridAdapter.ViewHolder, view: View, data: T_Photo, position: Int, isChecked: Boolean)
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
                if(isMoneyType){
                    if(isAll){
                        if(adapterPosition == 0){ //여행 전
                            moneyItemClickListener!!.onButtonClick(this, it, null, adapterPosition)
                        }
                        else{
                            moneyItemClickListener!!.onButtonClick(this, it, getItem(adapterPosition - 1)!!, adapterPosition)
                        }
                    }
                    else{
                        moneyItemClickListener!!.onButtonClick(this, it, getItem(adapterPosition)!!, adapterPosition)
                    }

                }
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
        if(p0 is ViewHolder) {
            var item: T_Day? = null
            if(isMoneyType && isAll){
                if(p1 != 0){
                    item = getItem(p1 - 1)!!
                }
            }
            else{
                item = getItem(p1)!!
            }

            if(item != null){
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
            }
            else{
                p0.dayNumText.textSize = 30f
                p0.dayNumText.text = "여행 전"
                p0.dateText.visibility = View.GONE
            }

            if(isMoneyType){
                var result:RealmResults<T_Money>
                if(item != null){
                    result = realm.where(T_Money::class.java).equalTo("listID", item.listID).equalTo("dayNum", item.num).findAll()!!.sort("dateTime")
                }
                else{
                    val num = -1
                    result = realm.where(T_Money::class.java).equalTo("listID", (context as ShowMoneyActivity).ListID).equalTo("dayNum", num).findAll()!!.sort("dateTime")
                }
                result.addChangeListener{_, _->
                    totalViewChangeListener!!.onTotalViewChange(p1)
                }
                val moneyAdapter = MoneyGridAdapter(result, context)
                moneyAdapter.itemClickListener = object :MoneyGridAdapter.OnItemClickListener{
                    override fun onItemClick(
                            holder: MoneyGridAdapter.ViewHolder, view: View, data: T_Money, position: Int, isChecked: Boolean) {
                        moneyItemClickListener!!.onMoneyItemClick(holder, view, data, position, isChecked)
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
                val result = realm.where(T_Photo::class.java).equalTo("listID", item!!.listID).equalTo("dayNum", item!!.num).findAll()!!.sort("dateTime")
                val photoAdapter = PhotoGridAdapter(result, context)
                photoAdapter.itemClickListener = object :PhotoGridAdapter.OnItemClickListener{
                    override fun onItemClick(
                            holder: PhotoGridAdapter.ViewHolder,
                            view: View,
                            data: T_Photo,
                            position: Int,
                            isChecked: Boolean
                    ) {
                        if((context as ShowPhotoActivity).deleteMode){
                            photoItemClickListener!!.onPhotoItemClick(holder, view, data, position, isChecked)
                        }
                        else{
                            val intent = Intent(view.context, DetailPhotoActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra("isAll", isAll)
                            intent.putExtra("photoId", data.id)
                            intent.putExtra("listId", item!!.listID)
                            intent.putExtra("dayNum", item!!.num)
                            view.context.startActivity(intent)
                        }
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

    override fun getItemCount(): Int {
        if(isAll && isMoneyType){
            return super.getItemCount() + 1

        }
        else{
            return super.getItemCount()
        }
    }
}

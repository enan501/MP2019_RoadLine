package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmRecyclerViewAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.money.ShowMoneyActivity
import kotlinx.android.synthetic.main.activity_add_money.*
import org.w3c.dom.Text
import java.text.DecimalFormat
import java.time.chrono.IsoChronology
import kotlin.math.roundToInt

class MoneyGridAdapter (realmResult: OrderedRealmCollection<T_Money>, val context: Context) : RealmRecyclerViewAdapter<T_Money, MoneyGridAdapter.ViewHolder>(realmResult, true) {
    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, view: View, data: T_Money, position: Int, isChecked: Boolean)
    }

    lateinit var itemClickListener: OnItemClickListener
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var priceImage:ImageView
        var priceText:TextView
        var checkButton:ImageView
        var korPriceText:TextView
        var isChecked = false
        init {
            priceImage = itemView.findViewById(R.id.priceImage)
            priceText = itemView.findViewById(R.id.priceText)
            checkButton = itemView.findViewById(R.id.checkButton)
            korPriceText = itemView.findViewById(R.id.korPriceText)
            itemView.setOnClickListener {
                itemClickListener.onItemClick(this, it, getItem(adapterPosition)!!, adapterPosition, isChecked)
                if((context as ShowMoneyActivity).deleteMode)
                    isChecked = !isChecked
            }

            itemView.setOnLongClickListener {
                true
            }

            itemView.setOnTouchListener { v, event ->
                val item = getItem(adapterPosition)!!
                when(event.action){
                    MotionEvent.ACTION_DOWN->{
                        if(item.currency!!.code != "KRW" && !(context as ShowMoneyActivity).deleteMode){
                            korPriceText.visibility = View.VISIBLE
                            val korPrice = item.price * item.currency!!.rate
                            if(korPrice.roundToInt().toString().length >= 6){
                                korPriceText.text = shortFormat.format(korPrice) + "  ₩"
                            }
                            else{
                                korPriceText.text = longFormat.format(korPrice) + "  ₩"
                            }
                        }
                    }
                    MotionEvent.ACTION_UP->{
                        korPriceText.visibility = View.INVISIBLE
                    }
                }
                false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.grid_money_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if(p0 is ViewHolder){
            Log.d("mytag", "moneyGrid: onBindViewHolder")
            p0.isChecked = false
            p0.checkButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            val item = getItem(p1)!!
            if(item.img == ""){
                when(item.cate){
                    "식사" -> p0.priceImage.setImageResource(R.drawable.meal)
                    "쇼핑" -> p0.priceImage.setImageResource(R.drawable.shopping)
                    "교통" -> p0.priceImage.setImageResource(R.drawable.transport)
                    "관광" -> p0.priceImage.setImageResource(R.drawable.tour)
                    "숙박" -> p0.priceImage.setImageResource(R.drawable.lodgment)
                    "기타" -> p0.priceImage.setImageResource(R.drawable.etc)
                }
            }
            else{
                Glide.with(context).load(item.img).into(p0.priceImage)
            }
            val num = item.price
            if(num.roundToInt().toString().length >= 6){
                p0.priceText.text = shortFormat.format(num) + " " + item.currency!!.symbol
            }
            else{
                p0.priceText.text = longFormat.format(num) + " " + item.currency!!.symbol
            }
            if((context as ShowMoneyActivity).deleteMode){
                p0.checkButton.visibility = View.VISIBLE
                p0.checkButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }
            else{
                p0.checkButton.visibility = View.INVISIBLE
            }
        }
    }
}
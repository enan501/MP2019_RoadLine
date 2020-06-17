package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmRecyclerViewAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.R
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MoneyGridAdapter (realmResult: OrderedRealmCollection<T_Money>, val context: Context) : RealmRecyclerViewAdapter<T_Money, MoneyGridAdapter.ViewHolder>(realmResult, true) {
    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, view: View, data: T_Money, position: Int)
    }

    lateinit var itemClickListener: OnItemClickListener
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var priceImage:ImageView
        var priceText:TextView
        init {
            priceImage = itemView.findViewById(R.id.priceImage)
            priceText = itemView.findViewById(R.id.priceText)
            itemView.setOnClickListener {
                itemClickListener.onItemClick(this, it, getItem(adapterPosition)!!, adapterPosition)
            }
            itemView.setOnLongClickListener {
                removeItem(adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.grid_money_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if(p0 is ViewHolder){
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
        }
    }

    fun removeItem(position: Int){
        val item = getItem(position)!!
        Realm.init(context)
        val realm = Realm.getDefaultInstance()

        val builder = AlertDialog.Builder(context)
        builder.setMessage("삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialogInterface, _ ->
                    realm.beginTransaction()
                    item.deleteFromRealm()
                    realm.commitTransaction()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
        val dialog = builder.create()
        dialog.show()
    }

}
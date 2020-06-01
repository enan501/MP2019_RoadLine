package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.Realm
import io.realm.RealmList
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_Money
import konkukSW.MP2019.roadline.R
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MoneyGridAdapter (val context: Context, val items: RealmList<T_Money>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, view: View, data: T_Money, position: Int)
    }

    lateinit var itemClickListener: OnItemClickListener
    val shortFormat = DecimalFormat("###,###")
    val longFormat = DecimalFormat("###,###.##")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var priceImage:ImageView
        var priceText:TextView
        init {
            priceImage = itemView.findViewById(R.id.priceImage)
            priceText = itemView.findViewById(R.id.priceText)
            itemView.setOnClickListener {
                itemClickListener.onItemClick(this, it, items[adapterPosition]!!, adapterPosition)
            }
            itemView.setOnLongClickListener {
                removeItem(adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.grid_money_item, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if(p0 is ViewHolder){
            if(items.get(p1)!!.img == ""){
                when(items.get(p1)!!.cate){
                    "식사" -> p0.priceImage.setImageResource(R.drawable.meal)
                    "쇼핑" -> p0.priceImage.setImageResource(R.drawable.shopping)
                    "교통" -> p0.priceImage.setImageResource(R.drawable.transport)
                    "관광" -> p0.priceImage.setImageResource(R.drawable.tour)
                    "숙박" -> p0.priceImage.setImageResource(R.drawable.lodgment)
                    "기타" -> p0.priceImage.setImageResource(R.drawable.etc)
                }
            }
            else{
                p0.priceImage.setImageBitmap(BitmapFactory.decodeFile(items.get(p1)!!.img))
            }
            val num = items.get(p1)!!.price * (1 / items.get(p1)!!.currency!!.rate)
            if(num.toString().length >= 6){
                p0.priceText.text = shortFormat.format(num) + " " + items.get(p1)!!.currency!!.symbol
            }
            else{
                p0.priceText.text = longFormat.format(num) + " " + items.get(p1)!!.currency!!.symbol
            }
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun removeItem(position: Int){
        val listId = items[position]!!.listID
        val dayNum = items[position]!!.dayNum

        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        Log.i("RealmManager", realm.path)

        val builder = AlertDialog.Builder(context)
        builder.setMessage("삭제하시겠습니까?")
                .setPositiveButton("삭제") { dialogInterface, _ ->
                    realm.beginTransaction()
                    items.removeAt(position)
                    realm.commitTransaction()

                    realm.beginTransaction()
                    realm.where(T_Money::class.java).equalTo("listID", listId).equalTo("dayNum", dayNum).findFirst()!!.deleteFromRealm()
                    realm.commitTransaction()

                    notifyItemRangeRemoved(0, itemCount + 1)
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
        val dialog = builder.create()
        dialog.show()
    }

}
package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Photo
import konkukSW.MP2019.roadline.R

class PhotoGridAdapter (realmResult: OrderedRealmCollection<T_Photo>, val context: Context) : RealmRecyclerViewAdapter<T_Photo, PhotoGridAdapter.ViewHolder>(realmResult, true) {
    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, view: View, data: T_Photo, position: Int)
    }

    lateinit var itemClickListener: OnItemClickListener

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var priceImage: ImageView
        var priceText: TextView
        var imgCover:ImageView
        init {
            priceImage = itemView.findViewById(R.id.priceImage)
            priceText = itemView.findViewById(R.id.priceText)
            imgCover = itemView.findViewById(R.id.img_cover)
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
            Glide.with(context).load(item.img).into(p0.priceImage)
            p0.priceText.visibility = View.GONE
            p0.imgCover.visibility = View.GONE
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
                    realm.where(T_Photo::class.java).equalTo("id", item.id).findFirst()!!.deleteFromRealm()
                    realm.commitTransaction()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
        val dialog = builder.create()
        dialog.show()
    }

}
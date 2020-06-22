package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Photo
import konkukSW.MP2019.roadline.R

class PhotoPickGridAdapter (realmResult: OrderedRealmCollection<T_Photo>, val context: Context) : RealmRecyclerViewAdapter<T_Photo, PhotoPickGridAdapter.ViewHolder>(realmResult, true) {
    interface OnItemClickListener {
        fun onItemClick(data: T_Photo, clickedPos: Int)
        fun onNothingClicked()
    }

    lateinit var itemClickListener: OnItemClickListener
    var clickedPos = -1


    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var photoImage: ImageView
        var backImage: ImageView
//        var clickedState = false
        init {
            photoImage = itemView.findViewById(R.id.photoImage)
            backImage = itemView.findViewById(R.id.backImage)
            itemView.setOnClickListener {
//                clickedState = !clickedState
//                Log.d("mytag", adapterPosition.toString() + " : " + clickedState.toString() + " : " + clickedPos.toString())
                if(adapterPosition != clickedPos){ //클릭
                    backImage.visibility = View.VISIBLE
                    itemClickListener.onItemClick(getItem(adapterPosition)!!, clickedPos)
                    clickedPos = adapterPosition
                }
                else{ //클릭 취소
                    backImage.visibility = View.INVISIBLE
                    clickedPos = -1
                    itemClickListener.onNothingClicked()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.grid_image_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if(p0 is ViewHolder){
            val item = getItem(p1)!!
            Glide.with(context).load(item.img).into(p0.photoImage)
            if(p1 == clickedPos){
                p0.backImage.visibility = View.VISIBLE
            }
            else{
                p0.backImage.visibility = View.INVISIBLE
            }
        }
    }
}


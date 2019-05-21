package konkukSW.MP2019.roadline.Data.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.MainList
import konkukSW.MP2019.roadline.R

class MainListAdapter(var items:ArrayList<MainList>): RecyclerView.Adapter<MainListAdapter.ViewHolder>() {
    
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.item_main_list,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.title.text =items.get(p1).title
        p0.date.text = items.get(p1).date
        if(items.get(p1).image.isEmpty()){
            //TODO("대표이미지 설정")
            p0.image.setImageResource(R.drawable.ml_default_image)
        }
        else{
            //TODO("이미지 경로 받아와서 넣어주기")
        }
    }
    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, data: MainList, position: Int)
    }
    var itemClickListener : OnItemClickListener? = null

    inner class ViewHolder(itemView: View)
        :RecyclerView.ViewHolder(itemView){
        var title: TextView
        var date: TextView
        var image: ImageView
        init{
            title = itemView.findViewById(R.id.MLItem_title)
            date = itemView.findViewById(R.id.MLItem_date)
            image = itemView.findViewById(R.id.MLItem_image)
            itemView.setOnClickListener{
                val position = adapterPosition
                itemClickListener?.OnItemClick(this,items[position],position)
            }

        }

    }
}

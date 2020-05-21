package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.R

class DateIconListAdapter(val size:Int, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon:ImageView

        init {
            icon = itemView.findViewById(R.id.ri_icon)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_icon,p0,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if(p0 is ViewHolder){
            if(p1 < size){
                if(size == 1){
                    p0.icon.setImageResource(R.drawable.ver_one)
                }
                else{
                    if(p1 == 0){
                        p0.icon.setImageResource(R.drawable.ver_top)
                    }
                    else if(p1 == size-1){
                        p0.icon.setImageResource(R.drawable.ver_down)
                    }
                    else{
                        p0.icon.setImageResource(R.drawable.ver_mid)
                    }
                }
            }
            else{
                p0.icon.setImageResource(R.drawable.transparent_background)
            }
        }
    }

    override fun getItemCount(): Int {
        return size + 1
    }
}
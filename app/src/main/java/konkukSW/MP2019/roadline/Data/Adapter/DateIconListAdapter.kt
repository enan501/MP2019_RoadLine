package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Extension.setVisible
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.row_icon.view.*

class DateIconListAdapter(val size:Int, val context: Context): androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        //var icon:ImageView

        init {
            //icon = itemView.findViewById(R.id.ri_icon)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.row_icon,p0,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
        if(p0 is ViewHolder){
            if(p1 < size){
                if(size == 1){
                    //p0.icon.setImageResource(R.drawable.ver_one)
                    p0.itemView.barTop.setVisible(false)
                    p0.itemView.barBottom.setVisible(false)
                }
                else{
                    if(p1 == 0){
                        //p0.icon.setImageResource(R.drawable.ver_top)
                        p0.itemView.barTop.setVisible(false)
                        p0.itemView.barBottom.setVisible(true)
                    }
                    else if(p1 == size-1){
                        //p0.icon.setImageResource(R.drawable.ver_down)
                        p0.itemView.barTop.setVisible(true)
                        p0.itemView.barBottom.setVisible(false)
                    }
                    else{
                        //p0.icon.setImageResource(R.drawable.ver_mid)
                        p0.itemView.barTop.setVisible(true)
                        p0.itemView.barBottom.setVisible(true)
                    }
                }
            }
            else{
                p0.itemView.barTop.setVisible(false)
                p0.itemView.barBottom.setVisible(false)
                p0.itemView.circle.setVisible(false)
                //p0.icon.setImageResource(R.drawable.transparent_background)
            }
        }
    }

    override fun getItemCount(): Int {
        return size + 1
    }
}
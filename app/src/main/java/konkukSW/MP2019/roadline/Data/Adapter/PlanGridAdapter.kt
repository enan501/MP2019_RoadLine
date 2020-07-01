package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Extension.setVisible
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.widget.BaseDialog
import kotlinx.android.synthetic.main.row_plan.view.*

class PlanGridAdapter(realmResult: OrderedRealmCollection<T_Plan>, val context: Context) :
        RealmRecyclerViewAdapter<T_Plan, PlanGridAdapter.ViewHolder>(realmResult, false) {

    var count = 0 //realmResult의 count
    var humanIndex = -1

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View, data: T_Plan, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null
    var realm: Realm

    init {
        Realm.init(context)
        realm = Realm.getDefaultInstance()
    }

    inner class ViewHolder(itemView: View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var humanImg: ImageView

        init {
            name = itemView.findViewById(R.id.plan_Item_textView)
            humanImg = itemView.findViewById(R.id.man_img)
            itemView.setOnClickListener {
                val rPos = convertPos(adapterPosition)
                Log.d("mytag", rPos.toString() + " " + count.toString())
                if (rPos <= count - 1) {
                    itemClickListener?.OnItemClick(this, it, getItem(rPos)!!, adapterPosition)
                }
            }

            itemView.setOnLongClickListener {
                val rPos = convertPos(adapterPosition)
                removeItem(rPos)
                true
            }

        }
    }

    fun removeItem(position: Int) {
        val item = getItem(position)!!
        val builder = BaseDialog.Builder(context).create()
        builder.setTitle("알림")
                .setMessage("삭제하시겠습니까?")
                .setOkButton("삭제", View.OnClickListener {
                    realm.beginTransaction()
                    realm.where(T_Plan::class.java).equalTo(
                            "id",
                            item.id
                    ).findFirst()!!.deleteFromRealm()
                    realm.commitTransaction()
                    builder.dismissDialog()
                })
                .setCancelButton("취소")
                .show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_plan, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val rPos = convertPos(position)

            if (rPos > count - 1) { //empty
                holder.humanImg.visibility = View.INVISIBLE
                //holder.roadImg.visibility = View.INVISIBLE
                holder.itemView.barTop.setVisible(false)
                holder.itemView.barLeft.setVisible(false)
                holder.itemView.barBottom.setVisible(false)
                holder.itemView.barRight.setVisible(false)
                holder.itemView.circle.setVisible(false)
                holder.name.visibility = View.INVISIBLE
            } else {
                if (position == humanIndex)
                    holder.humanImg.visibility = View.VISIBLE
                else
                    holder.humanImg.visibility = View.INVISIBLE
                //holder.roadImg.visibility = View.VISIBLE
                holder.itemView.circle.setVisible(true)
                holder.name.visibility = View.VISIBLE
                val item = getItem(rPos)!!
                if (item.nameAlter == null) {
                    holder.name.text = item.name
                } else {
                    holder.name.text = item.nameAlter
                }


                if (rPos == count - 1) { //마지막
                    when (position % 10) {
                        1, 2, 3, 4 -> {
                            //holder.roadImg.setImageResource(R.drawable.road_right_end)
                            holder.itemView.barTop.setVisible(false)
                            holder.itemView.barLeft.setVisible(true)
                            holder.itemView.barBottom.setVisible(false)
                            holder.itemView.barRight.setVisible(false)
                        }
                        5, 6, 7, 8 -> {
                            //holder.roadImg.setImageResource(R.drawable.road_left_end)
                            holder.itemView.barTop.setVisible(false)
                            holder.itemView.barLeft.setVisible(false)
                            holder.itemView.barBottom.setVisible(false)
                            holder.itemView.barRight.setVisible(true)
                        }
                        9 -> {
                            //holder.roadImg.setImageResource(R.drawable.road_right_down_end)
                            holder.itemView.barTop.setVisible(true)
                            holder.itemView.barLeft.setVisible(false)
                            holder.itemView.barBottom.setVisible(false)
                            holder.itemView.barRight.setVisible(false)
                        }
                        0 -> {
                            //holder.roadImg.setImageResource(R.drawable.road_left_down_end)
                            holder.itemView.barTop.setVisible(true)
                            holder.itemView.barLeft.setVisible(false)
                            holder.itemView.barBottom.setVisible(false)
                            holder.itemView.barRight.setVisible(false)
                        }
                    }
                    if (position == 0) {
                        //holder.roadImg.setImageResource(R.drawable.plan_one)
                        holder.itemView.barTop.setVisible(false)
                        holder.itemView.barLeft.setVisible(false)
                        holder.itemView.barBottom.setVisible(false)
                        holder.itemView.barRight.setVisible(false)
                    }
                } else {
                    when (position % 10) {
                        1, 2, 3, 6, 7, 8 -> {
                            //holder.roadImg.setImageResource(R.drawable.road_midle)
                            holder.itemView.barTop.setVisible(false)
                            holder.itemView.barLeft.setVisible(true)
                            holder.itemView.barBottom.setVisible(false)
                            holder.itemView.barRight.setVisible(true)
                        }
                        5 -> {
                            //holder.roadImg.setImageResource(R.drawable.road_left_down)
                            holder.itemView.barTop.setVisible(false)
                            holder.itemView.barLeft.setVisible(false)
                            holder.itemView.barBottom.setVisible(true)
                            holder.itemView.barRight.setVisible(true)
                        }
                        0 -> {
                            //holder.roadImg.setImageResource(R.drawable.road_left_up)
                            holder.itemView.barTop.setVisible(true)
                            holder.itemView.barLeft.setVisible(false)
                            holder.itemView.barBottom.setVisible(false)
                            holder.itemView.barRight.setVisible(true)
                        }
                        4 -> {
                            //holder.roadImg.setImageResource(R.drawable.road_right_down)
                            holder.itemView.barTop.setVisible(false)
                            holder.itemView.barLeft.setVisible(true)
                            holder.itemView.barBottom.setVisible(true)
                            holder.itemView.barRight.setVisible(false)
                        }
                        9 -> {
                            //holder.roadImg.setImageResource(R.drawable.roadl_right_up)
                            holder.itemView.barTop.setVisible(true)
                            holder.itemView.barLeft.setVisible(true)
                            holder.itemView.barBottom.setVisible(false)
                            holder.itemView.barRight.setVisible(false)
                        }
                        else -> {
                        }
                    }
                    if (position == 0) {
                        //holder.roadImg.setImageResource(R.drawable.road_left_end)
                        holder.itemView.barTop.setVisible(false)
                        holder.itemView.barLeft.setVisible(false)
                        holder.itemView.barBottom.setVisible(false)
                        holder.itemView.barRight.setVisible(true)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        count = super.getItemCount()
        if (count % 10 in 6..9) {
            return (count / 10 + 1) * 10
        } else {
            return count
        }
    }

    fun convertPos(position: Int): Int {
        when (position % 10) {
            5 -> return position + 4
            6 -> return position + 2
            8 -> return position - 2
            9 -> return position - 4
            else -> return position
        }
    }

    fun setHuman(location: android.location.Location?): Int {
        val clat = location!!.latitude
        val clng = location.longitude
        var latDif = getItem(0)!!.locationY - clat
        var lngDif = getItem(0)!!.locationX - clng
        var dif = latDif * latDif + lngDif * lngDif
        var min = dif
        var minIndex = 0
        for (i in 1..count - 1) {
            latDif = getItem(i)!!.locationY - clat
            lngDif = getItem(i)!!.locationX - clng
            dif = latDif * latDif + lngDif * lngDif
            if (min > dif) {
                min = dif
                minIndex = i
            }
        }
        humanIndex = convertPos(minIndex)
        notifyItemChanged(humanIndex)
        return convertPos(minIndex)
    }


}
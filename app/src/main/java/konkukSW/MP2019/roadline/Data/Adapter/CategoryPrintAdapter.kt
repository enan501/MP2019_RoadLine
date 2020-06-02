package konkukSW.MP2019.roadline.Data.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import konkukSW.MP2019.roadline.Data.Dataclass.CategoryTotal
import konkukSW.MP2019.roadline.R

class CategoryPrintAdapter (context: Context, resource:Int, val list:ArrayList<CategoryTotal>)
    : ArrayAdapter<CategoryTotal>(context, resource, list) // 상속받은 부모한테 전달
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v: View? = convertView
        if (v == null) {
            val vi = context.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(R.layout.row_category_money, null)
        }

        val p = list[position]
        v!!.findViewById<TextView>(R.id.category).text = p.category
        v!!.findViewById<TextView>(R.id.price).text = p.price

        return v // 한줄이 채워진 상태가 됨
    }
}

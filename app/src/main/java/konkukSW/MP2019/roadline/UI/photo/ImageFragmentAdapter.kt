package konkukSW.MP2019.roadline.UI.photo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ImageFragmentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    val items = ArrayList<Fragment>()
    override fun getItem(p0: Int): Fragment {
        return items[p0]
    }

    override fun getCount(): Int {
        return items.size
    }

    fun addItem(item: Fragment){
        items.add(item)
    }

    fun clearItem(){
        items.clear()
    }

    fun removeItem(p: Int){
        items.removeAt(p)
        notifyDataSetChanged()
    }
}
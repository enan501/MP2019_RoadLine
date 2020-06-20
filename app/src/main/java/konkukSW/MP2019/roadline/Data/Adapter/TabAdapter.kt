package konkukSW.MP2019.roadline.Data.Adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import konkukSW.MP2019.roadline.UI.date.Fragment1
import konkukSW.MP2019.roadline.UI.date.Fragment2
import konkukSW.MP2019.roadline.UI.date.Fragment4

class TabAdapter(fm: FragmentManager, val num:Int): androidx.fragment.app.FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return num
    }

    override fun getItem(position: Int): Fragment {
        when(position){
            0->return Fragment1()
            1->return Fragment2()
            2->return Fragment4()
            else -> {
                Log.d("mytag", "Error : fragment null")
                return Fragment1()
            }
        }
    }
}
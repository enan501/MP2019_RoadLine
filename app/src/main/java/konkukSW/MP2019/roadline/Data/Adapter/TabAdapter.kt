package konkukSW.MP2019.roadline.Data.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import konkukSW.MP2019.roadline.UI.date.Fragment1
import konkukSW.MP2019.roadline.UI.date.Fragment2
import konkukSW.MP2019.roadline.UI.date.Fragment4

class TabAdapter(fm: androidx.fragment.app.FragmentManager, val num:Int): androidx.fragment.app.FragmentPagerAdapter(fm) {
//    override fun getItemPosition(p0: Any): Int {
//        return PagerAdapter.POSITION_NONE
//    }
    override fun getCount(): Int {
        return num
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment? {
        when(position){
            0->return Fragment1()
            1->return Fragment2()
            //2->return Fragment3()
            2->return Fragment4()

        }
        return null
    }
}
package konkukSW.MP2019.roadline.Data.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import konkukSW.MP2019.roadline.UI.date.Fragment1
import konkukSW.MP2019.roadline.UI.date.Fragment2
import konkukSW.MP2019.roadline.UI.date.Fragment4

class TabAdapter(fm: FragmentManager, val num:Int): FragmentPagerAdapter(fm) {
//    override fun getItemPosition(p0: Any): Int {
//        return PagerAdapter.POSITION_NONE
//    }
    override fun getCount(): Int {
        return num
    }

    override fun getItem(position: Int): Fragment? {
        when(position){
            0->return Fragment1()
            1->return Fragment2()
            //2->return Fragment3()
            2->return Fragment4()

        }
        return null
    }
}
package konkukSW.MP2019.roadline.Data.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.ListFragment
import konkukSW.MP2019.roadline.UI.date.*

class TabAdapter(fm: FragmentManager, val num:Int): FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return num
    }

    override fun getItem(position: Int): Fragment? {
        when(position){
            0->return Fragment1()
            1->return Fragment2()
            2->return Fragment3()
            3->return Fragment4()
        }
        return null
    }
}
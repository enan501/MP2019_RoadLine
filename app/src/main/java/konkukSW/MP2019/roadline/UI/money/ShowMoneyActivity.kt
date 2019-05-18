package konkukSW.MP2019.roadline.UI.money

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import konkukSW.MP2019.roadline.R

var data:ArrayList<MyCafe> = ArrayList()
lateinit var adapter:MyCafeAdapter

class ShowMoneyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_money)
    }
}

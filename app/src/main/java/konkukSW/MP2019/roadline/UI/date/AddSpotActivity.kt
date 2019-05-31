package konkukSW.MP2019.roadline.UI.date

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_add_spot.*

class AddSpotActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var addMap: GoogleMap
    lateinit var addMapView:SupportMapFragment
    override fun onMapReady(p0: GoogleMap) {
        addMap = p0
    }

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)
        init()
    }

    fun init(){
        addMapView = supportFragmentManager.findFragmentById(R.id.AS_MapView) as SupportMapFragment
        addMapView.getMapAsync(this)
        initListener()
    }

    fun initListener(){
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        as_button.setOnClickListener {
            realm.beginTransaction()
            val plan: T_Plan = realm.createObject(T_Plan::class.java)
            plan.listNum = 0
            plan.dayNum = 0
            plan.num = 0
            plan.name = as_spotName.text.toString()
            plan.time = as_time.text.toString()
            plan.memo = as_memo.text.toString()
            realm.commitTransaction()
            val i = Intent(this, ShowDateActivity::class.java)
            startActivity(i)
        }

    }
}

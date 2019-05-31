package konkukSW.MP2019.roadline.UI.date

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import konkukSW.MP2019.roadline.R

class AddSpotActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onMapReady(p0: GoogleMap?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)
        init()
    }

    fun init(){
        val addMap = supportFragmentManager.findFragmentById(R.id.addMapView) as SupportMapFragment
        addMap.getMapAsync(this)
    }
}

package konkukSW.MP2019.roadline.UI.date

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_add_spot.*
import java.util.*

class AddSpotActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var addMap: GoogleMap
    lateinit var addMapView:SupportMapFragment
    var spotId:String = ""
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
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.api_key))
        }
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.AS_SearchBox) as AutocompleteSupportFragment?

        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(status: Status) {
                Log.d("addMap", "Error : " + status.toString())
            }

            override fun onPlaceSelected(place: Place) {
                addMap.clear()
                addMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng,12f))
                var marker = MarkerOptions()
                marker.position(place.latLng!!)
                addMap.addMarker(marker)
                as_spotName.setText(place.name)
            }
        })
        addMapView = supportFragmentManager.findFragmentById(R.id.AS_MapView) as SupportMapFragment
        addMapView.getMapAsync(this)
        initData()
        initListener()
    }

    fun initListener(){
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        as_button.setOnClickListener { //추가 등록일때
            realm.beginTransaction()
            if(as_button.text == "등록"){
                val plan: T_Plan = realm.createObject(T_Plan::class.java)
                plan.listID = "a"
                plan.dayNum = 0
                plan.id = "a"
                plan.name = as_spotName.text.toString()
                plan.time = as_time.text.toString()
                plan.memo = as_memo.text.toString()
            }
            else{ //수정
                val result:T_Plan  = realm.where(T_Plan::class.java).equalTo("num", spotId).findFirst()!!
                result.name = as_spotName.text.toString()
                result.time = as_time.text.toString()
                result.memo = as_memo.text.toString()
            }
            realm.commitTransaction()
            val i = Intent(this, ShowDateActivity::class.java)
            startActivity(i)
        }
    }

    fun initData(){
        var spot = intent.getSerializableExtra("spot")
        if(spot!=null){ //수정
            spot = spot as Plan
            spotId = spot.id
            as_spotName.setText(spot.name)
            as_time.setText(spot.time)
            as_memo.setText(spot.memo)
            as_button.setText("수정")
            val as_searchBox = AS_SearchBox.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
            as_searchBox.setText(spot.name)

        }
    }
}

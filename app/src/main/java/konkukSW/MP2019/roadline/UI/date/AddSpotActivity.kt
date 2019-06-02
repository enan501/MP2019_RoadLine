package konkukSW.MP2019.roadline.UI.date

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
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
    var spotName:String=""
    var locationX:Double = 0.0
    var locationY:Double = 0.0
    var btnType:Boolean = false
    var time:String = ""
    var memo:String = ""

    override fun onMapReady(p0: GoogleMap) {
        addMap = p0
        initData()
        initListener()
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
                spotName = place.name.toString()
                locationY = (place.latLng as LatLng).latitude
                locationX = (place.latLng as LatLng).longitude
            }
        })
        addMapView = supportFragmentManager.findFragmentById(R.id.AS_MapView) as SupportMapFragment
        addMapView.getMapAsync(this)
    }

    fun initListener(){
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        as_button_check.setOnClickListener { //체크 등록 버튼
            val as_searchBox = AS_SearchBox.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
            if(as_searchBox.text.toString() != ""){
                realm.beginTransaction()
                if(btnType == false){ //등록
                    val plan: T_Plan = realm.createObject(T_Plan::class.java)
                    plan.listID = "a"
                    plan.dayNum = 0
                    plan.id = UUID.randomUUID().toString()
                    plan.name = spotName
                    plan.time = time
                    plan.memo = memo
                    plan.locationX = locationX
                    plan.locationY = locationY
                    plan.pos = intent.getIntExtra("pos", -1)
                }
                else{ //수정
                    Log.v("rea", "kk")
                    val result:T_Plan  = realm.where(T_Plan::class.java).equalTo("id", spotId).findFirst()!!
                    result.name = spotName
                    result.time = time
                    result.memo = memo
                    result.locationX = locationX
                    result.locationY = locationY
                }
                realm.commitTransaction()
                val s = Intent()
                setResult(Activity.RESULT_OK, s)
                finish()
            }
            else{ //아무값 입력하지 않으면
                onBackPressed()
            }

        }

        as_button_plus.setOnClickListener { //상세정보 추가 버튼
            val builder = AlertDialog.Builder(this) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
            val addDialog = layoutInflater.inflate(R.layout.add_plan_dialog, null)
            val dialogMemo = addDialog.findViewById<EditText>(R.id.apd_editText1)
            val dialogTime = addDialog.findViewById<EditText>(R.id.apd_editText2)
            if(btnType){ //수정
                dialogMemo.setText(memo)
                dialogTime.setText(time)
            }
            builder.setView(addDialog)
                .setPositiveButton("추가") { dialogInterface, i ->
                    memo = dialogMemo.text.toString()
                    time = dialogTime.text.toString()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
    }

    fun initData(){
        var spot = intent.getSerializableExtra("spot")
        if(spot!=null){ //수정
            spot = spot as Plan
            spotId = spot.id
            Realm.init(this)
            realm = Realm.getDefaultInstance()
            val result:T_Plan  = realm.where(T_Plan::class.java).equalTo("id", spotId).findFirst()!!
            spotName = result.name
            time = result.time
            memo = result.time
            locationX = result.locationX
            locationY = result.locationY
            btnType = true
            val as_searchBox = AS_SearchBox.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
            as_searchBox.setText(spotName)
            addMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationY,locationX),12f))
        }
    }
}

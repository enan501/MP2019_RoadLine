package konkukSW.MP2019.roadline.UI.date

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
import kotlinx.android.synthetic.main.activity_add_money.*
import kotlinx.android.synthetic.main.activity_add_spot.*
import kotlinx.android.synthetic.main.activity_show_date.*
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
    var hour:Int = 0
    var min:Int = 0
    var pos = -1
    var listID = ""
    var DayNum = 0
    val LOCATION_REQUEST = 1234
    lateinit var bitmapDraw:BitmapDrawable
    lateinit var b:Bitmap
    lateinit var markerIcon:Bitmap
    override fun onMapReady(p0: GoogleMap) {
        addMap = p0
        initData()
        initListener()
    }

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)
        initToolbar()
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initToolbar(){
        setSupportActionBar(as_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun init(){
        val i = intent
        pos = i.getIntExtra("pos", -1)
        listID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum",0)
        if(i.getIntExtra("path", 0) == 1 && pos > 0) // 경로 추천 버튼 추가
            path_bt.visibility = View.VISIBLE

        bitmapDraw = ContextCompat.getDrawable(this,R.drawable.marker) as BitmapDrawable
        b = bitmapDraw.bitmap
        markerIcon = Bitmap.createScaledBitmap(b, 71, 100, false)
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
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
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
                if(btnType == false){ //추가
                    val plan: T_Plan = realm.createObject(T_Plan::class.java)
                    plan.listID = listID
                    plan.dayNum = DayNum
                    plan.id = UUID.randomUUID().toString()
                    plan.name = spotName
                    plan.time = time
                    plan.memo = memo
                    plan.locationX = locationX
                    plan.locationY = locationY
                    plan.pos = pos
                }
                else{ //수정
                    val result:T_Plan  = realm.where(T_Plan::class.java).equalTo("id", spotId).findFirst()!!
                    result.name = spotName
                    result.time = time
                    result.memo = memo
                    result.locationX = locationX
                    result.locationY = locationY
                }
                realm.commitTransaction()
                val s = Intent()
                s.putExtra("dayNum", intent.getIntExtra("DayNum", -1))
                s.putExtra("listId", intent.getStringExtra("ListID"))
                setResult(Activity.RESULT_OK, s)

                finish()
            }
            else{ //아무값 입력하지 않으면
                onBackPressed()
            }
        }
        path_bt.setOnClickListener {
            var prev = realm.where(T_Plan::class.java).equalTo("listID",listID).equalTo("dayNum",DayNum).equalTo("pos",pos-1).findFirst()
            var cur = realm.where(T_Plan::class.java).equalTo("listID",listID).equalTo("dayNum",DayNum).equalTo("pos",pos).findFirst()

            var uri = "http://maps.google.com/maps?saddr="+prev!!.locationY+","+prev!!.locationX+"&daddr="+cur!!.locationY+","+cur!!.locationX+"&dirflg=r"
            var mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
        as_button_memo.setOnClickListener { //상세정보 추가 버튼
            val builder = AlertDialog.Builder(this) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
            val addDialog = layoutInflater.inflate(R.layout.add_memo_dialog, null)
            val dialogMemo = addDialog.findViewById<EditText>(R.id.apd_editText1)
            val dialogTime = addDialog.findViewById<TimePicker>(R.id.apd_timePicker)

            dialogMemo.setText(memo)
            dialogTime.hour = hour
            dialogTime.minute = min

            builder.setView(addDialog)
                .setPositiveButton("추가") { dialogInterface, i ->
                    memo = dialogMemo.text.toString()
                    hour = dialogTime.hour
                    min = dialogTime.minute
                    var min_zero = ""
                    if(min < 10)
                        min_zero = "0"
                    time = hour.toString() + ":" + min_zero + min.toString()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
        replace_bt.setOnClickListener {
            getCurLoc()
        }

//        as_button_time.setOnClickListener { //상세정보 추가 버튼
//            val builder = AlertDialog.Builder(this) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
//            val addDialog = layoutInflater.inflate(R.layout.add_time_dialog, null)
//            val dialogTime = addDialog.findViewById<TimePicker>(R.id.apd_timePicker)
//            dialogTime.hour = hour
//            dialogTime.minute = min
//            builder.setView(addDialog)
//                .setPositiveButton("추가") { dialogInterface, i ->
//                    hour = dialogTime.hour
//                    min = dialogTime.minute
//                    time = hour.toString() + "/"+min.toString()
//                }
//                .setNegativeButton("취소") { dialogInterface, i ->
//                }
//                .show()
//        }


    }
    fun getCurLoc(){
        if(checkAppPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))) {

            val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val providers = lm!!.getProviders(true)
            var location: android.location.Location? = null
            for (provider in providers) {
                val l = lm!!.getLastKnownLocation(provider) ?: continue
                if (location == null || l.getAccuracy() < location!!.getAccuracy()) {
                    // Found best last known location: %s", l);
                    location = l
                }
            }
            addMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location!!.latitude,location!!.longitude),12f))
        }
        else{
            initPermission()
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
            Log.v("timetag", time)
            if(time != ""){
                hour = Integer.parseInt(time.split(":").get(0))
                min = Integer.parseInt(time.split(":").get(1))
            }else{
                time = ""
            }
            memo = result.memo
            locationX = result.locationX
            locationY = result.locationY
            btnType = true
            val as_searchBox = AS_SearchBox.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
            as_searchBox.setText(spotName)
            addMap.addMarker(
                MarkerOptions()
                        .position(LatLng(locationY,locationX))
                        .title(spotName)
                        .snippet(hour.toString()+"시 "+min.toString()+"분")
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
            )
            addMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationY,locationX),12f))
        }else{
            addMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.552420, 126.984719),12f))
        }
    }


    fun checkAppPermission(requestPermission: Array<String>): Boolean {
        val requestResult = BooleanArray(requestPermission.size)
        for (i in requestResult.indices) {
            requestResult[i] = ContextCompat.checkSelfPermission(
                    this,
                    requestPermission[i]
            ) == PackageManager.PERMISSION_GRANTED
            if (!requestResult[i]) {
                return false
            }
        }
        return true
    } // checkAppPermission
    fun askPermission(requestPermission: Array<String>,
                      REQ_PERMISSION: Int) {
        ActivityCompat.requestPermissions(
                this, requestPermission,
                REQ_PERMISSION
        )
    } // askPermission
    override fun onRequestPermissionsResult(requestCode: Int, permissions:
    Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_REQUEST -> {
                if (checkAppPermission (permissions)) {
                    // 퍼미션 동의했을 때 할 일
                    Toast.makeText(this,"승인되었습니다", Toast.LENGTH_SHORT).show()
                    getCurLoc()
                } else {
                    Toast.makeText(this,"거부됨", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun initPermission(){
        if(checkAppPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))) {
        }
        else{
            val builder = AlertDialog.Builder(this)
            builder.setMessage("GPS 권한을 허용할까요?")
                    .setTitle("권한 요청")
                    .setIcon(konkukSW.MP2019.roadline.R.drawable.notification_action_background)
                    .setPositiveButton("OK"){
                        _,_ ->
                        askPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST)
                    }
            val dialog = builder.create()
            dialog.show()


        }
    }

}

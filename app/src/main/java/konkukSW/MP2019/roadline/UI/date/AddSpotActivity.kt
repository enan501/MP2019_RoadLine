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
import android.view.ViewGroup
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
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.R.id.places_autocomplete_search_input
import kotlinx.android.synthetic.main.activity_add_spot.*

import java.util.*

class AddSpotActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var addMap: GoogleMap

    var spotName:String = ""
    var locationX:Double = 0.0
    var locationY:Double = 0.0
    var memo:String ? = null
    var hour:Int? = null
    var minute:Int? = null

    var pos = -1
    var planId: String? = null
    var listID = ""
    var DayNum = 0
    val LOCATION_REQUEST = 1234

    lateinit var markerIcon:Bitmap
    var thisPlan:T_Plan? = null
    lateinit var builder: AlertDialog.Builder
    var autocompleteFragment: AutocompleteSupportFragment? = null
    lateinit var searchBox: EditText



    lateinit var dialogMemo : EditText
    lateinit var dialogTime : TimePicker
    lateinit var addDialog: View

    override fun onMapReady(p0: GoogleMap) {
        addMap = p0
        initMap()
        initListener()
    }

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spot)
        initData()
        initLayout()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initData(){
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        val i = intent
        pos = i.getIntExtra("pos", -1)
        listID = i.getStringExtra("ListID")
        DayNum = i.getIntExtra("DayNum",0)
        planId = i.getStringExtra("planId") //null이면 추가, null아니면 수정

        if(planId != null){ //수정
            thisPlan  = realm.where(T_Plan::class.java).equalTo("id", planId).findFirst()!!

            spotName = thisPlan!!.name
            hour = thisPlan!!.hour
            minute=  thisPlan!!.minute
            memo = thisPlan!!.memo
            locationX = thisPlan!!.locationX
            locationY = thisPlan!!.locationY
        }

        val bitmap = (ContextCompat.getDrawable(this,R.drawable.marker) as BitmapDrawable).bitmap
        markerIcon = Bitmap.createScaledBitmap(bitmap, 71, 100, false)
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.api_key))
        }
    }

    fun initLayout(){
        as_toolbar.title = "위치 추가"
        setSupportActionBar(as_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        builder = AlertDialog.Builder(this) //상세정보 추가 다이얼로그
        addDialog = layoutInflater.inflate(R.layout.add_memo_dialog, null)
        dialogMemo = addDialog.findViewById(R.id.apd_editText1)
        dialogTime = addDialog.findViewById(R.id.apd_timePicker)
        builder.setView(addDialog)

        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.AS_SearchBox) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG))
        searchBox = autocompleteFragment!!.view?.findViewById(places_autocomplete_search_input) as EditText
        val addMapView = supportFragmentManager.findFragmentById(R.id.AS_MapView) as SupportMapFragment
        addMapView.getMapAsync(this)

        if(pos > 0 && planId != null) // 경로 추천 버튼 추가
            path_bt.visibility = View.VISIBLE
    }


    fun initMap(){
        if(planId != null){ //수정
            searchBox.setText(thisPlan!!.name)
            var str:String?
            if(hour != null){
                val minute = minute.toString()
                str = hour.toString()
                if(minute.length == 1)
                    str += "시 0" + minute + "분"
                else
                    str += "시 " + minute + "분"

            }
            else{
                str = null
            }
            addMap.addMarker(
                    MarkerOptions()
                            .position(LatLng(locationY,locationX))
                            .title(spotName)
                            .snippet(str)
                            .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
            )
            addMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationY,locationX),12f))
        }
        else{ //추가
            addMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.552420, 126.984719),12f))
        }
    }

    fun initListener(){
        autocompleteFragment!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
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

        as_button_check.setOnClickListener { //체크 등록 버튼
            if(searchBox.text.toString() != ""){
                if(planId == null){ //추가
                    realm.beginTransaction()
                    val plan: T_Plan = realm.createObject(T_Plan::class.java, UUID.randomUUID().toString())
                    plan.listID = listID
                    plan.dayNum = DayNum
                    plan.name = spotName
                    plan.hour = hour
                    plan.minute = minute
                    plan.memo = memo
                    plan.locationX = locationX
                    plan.locationY = locationY
                    plan.pos = pos
                    realm.commitTransaction()
                }
                else{ //수정
                    realm.beginTransaction()
                    thisPlan!!.name = spotName
                    thisPlan!!.hour = hour
                    thisPlan!!.minute = minute
                    thisPlan!!.memo = memo
                    thisPlan!!.locationX = locationX
                    thisPlan!!.locationY = locationY
                    realm.commitTransaction()
                }
                val s = Intent()
                s.putExtra("dayNum", intent.getIntExtra("DayNum", -1))
                s.putExtra("listId", intent.getStringExtra("ListID"))
                setResult(Activity.RESULT_OK, s)

                finish()
            }
            else{ //아무값 입력하지 않으면
                Toast.makeText(applicationContext, "위치를 추가해주세요", Toast.LENGTH_LONG).show()
//                onBackPressed()
            }
        }
        path_bt.setOnClickListener {
            var prev = realm.where(T_Plan::class.java).equalTo("listID",listID).equalTo("dayNum",DayNum).equalTo("pos", pos - 1).findFirst()!!
            var cur = thisPlan

            var uri = "http://maps.google.com/maps?saddr="+prev.locationY+","+prev.locationX+"&daddr="+cur!!.locationY+","+cur!!.locationX+"&dirflg=r"
            var mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        memo_button.setOnClickListener {
            if(planId != null){ //수정
                dialogMemo.setText(thisPlan!!.memo)
                if(thisPlan!!.hour != null)
                    dialogTime.hour = thisPlan!!.hour!!
                else
                    dialogTime.hour = 0
                if(thisPlan!!.minute != null)
                    dialogTime.minute = thisPlan!!.minute!!
                else
                    dialogTime.minute = 0
            }
            else{
                dialogMemo.text.clear()
                //시간 초기화
                dialogTime.hour = 0
                dialogTime.minute = 0
            }
            if(addDialog.parent != null){
                (addDialog.parent as ViewGroup).removeView(addDialog)
            }
            builder.show()
        }

        builder.setPositiveButton("추가") { dialogInterface, i ->
            memo = dialogMemo.text.toString()
            hour = dialogTime.hour
            minute = dialogTime.minute
        }
        .setNegativeButton("취소") { dialogInterface, i -> }

        replace_bt.setOnClickListener {
            if(addDialog.parent != null){
                (addDialog.parent as ViewGroup).removeView(addDialog)
            }
            getCurLoc()
        }
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
                    .setIcon(R.drawable.notification_action_background)
                    .setPositiveButton("OK"){
                        _,_ ->
                        askPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST)
                    }
            val dialog = builder.create()
            dialog.show()
        }
    }

}

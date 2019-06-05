package konkukSW.MP2019.roadline.UI.date


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R


class Fragment4 : Fragment(),OnMapReadyCallback {
    var ListID = "init"
    var DayNum = 0;
    var spotList:ArrayList<Plan> = arrayListOf()
    var latlngList:ArrayList<LatLng> = arrayListOf()
    lateinit var gMap: GoogleMap
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(konkukSW.MP2019.roadline.R.layout.fragment_fragment4, container, false)
        val mapFragment = this.childFragmentManager.findFragmentById(konkukSW.MP2019.roadline.R.id.mapView) as SupportMapFragment
        initData()
        mapFragment.getMapAsync(this)
        return view
    }

    fun refresh(){
//        val ft = fragmentManager!!.beginTransaction()
//        ft.detach(this).attach(this).commit()

        spotList.clear()
        latlngList.clear()
        val mapFragment = this.childFragmentManager.findFragmentById(konkukSW.MP2019.roadline.R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        initData()


    }
    fun initData(){
        if(activity != null){
            val intent = activity!!.intent
            if(intent != null){
                ListID = intent.getStringExtra("ListID")
                DayNum = intent.getIntExtra("DayNum", 0)
                Log.v("logtag", ListID + DayNum.toString())
            }
        }
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        val results: RealmResults<T_Plan> = realm.where<T_Plan>(T_Plan::class.java)
            .equalTo("listID", ListID)
            .equalTo("dayNum", DayNum)
            .findAll()
            .sort("pos")
        for(T_Plan in results){
            spotList.add(Plan(T_Plan.listID, T_Plan.dayNum, T_Plan.id, T_Plan.name,
                T_Plan.locationX, T_Plan.locationY, T_Plan.time, T_Plan.memo, T_Plan.pos, -1, false))
            latlngList.add(LatLng(T_Plan.locationY,T_Plan.locationX))
        }
    }
    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        gMap.clear()
        addPolylines()
        addMarkers()
    }

    fun addPolylines(){
        val polyLine = gMap.addPolyline(
            PolylineOptions()
                .addAll(latlngList)
                .width(10f)
                .color(activity!!.getColor(R.color.blackAlpha))
                .startCap(ButtCap())
                .endCap(ButtCap())
        )
    }
    fun addMarkers(){
        var markerOptions = MarkerOptions()
        var boundsBuilder = LatLngBounds.builder()

            val bitmapDraw = ContextCompat.getDrawable(activity!!.applicationContext,R.drawable.marker) as BitmapDrawable
            val b = bitmapDraw.bitmap
            val marker = Bitmap.createScaledBitmap(b, 71, 100, false)

            for(i in 0..spotList.size-1)
            {
                markerOptions
                    .position(latlngList[i])
                .title(spotList[i].name)
                .snippet(spotList[i].time)
                .icon(BitmapDescriptorFactory.fromBitmap(marker))
            gMap.addMarker(markerOptions)

            boundsBuilder.include(latlngList[i])
        }
        if(spotList.isNotEmpty()){
            var bounds = boundsBuilder.build()
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100))
        }
    }




}

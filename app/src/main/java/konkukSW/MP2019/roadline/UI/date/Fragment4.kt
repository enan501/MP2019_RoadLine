package konkukSW.MP2019.roadline.UI.date


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import konkukSW.MP2019.roadline.R


class Fragment4 : androidx.fragment.app.Fragment(), OnMapReadyCallback {
    var ListID = "init"
    var DayNum = 0;
    var latlngList: ArrayList<LatLng> = arrayListOf()

    val mapFragment by lazy {
        this.childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
    }

    lateinit var gMap: GoogleMap

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(
                R.layout.fragment_fragment4,
                container,
                false
        )
        setObserve()
        initData()
        setList()
        mapFragment.getMapAsync(this)
        return view
    }


    fun setObserve() {
        (activity!! as ShowDateActivity).planResults.addChangeListener { _, _ ->
            setList()
            mapFragment.getMapAsync(this)
        }
    }

    fun setList() {
        latlngList.clear()
        for (T_Plan in (activity!! as ShowDateActivity).planResults) {
            latlngList.add(LatLng(T_Plan.locationY, T_Plan.locationX))
        }
    }

    fun initData() {
        if (activity != null) {
            val intent = activity!!.intent
            if (intent != null) {
                ListID = intent.getStringExtra("ListID")
                DayNum = intent.getIntExtra("DayNum", 0)
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        gMap.clear()
        addPolylines()
        addMarkers()
    }

    fun addPolylines() {
        gMap.addPolyline(
                PolylineOptions()
                        .addAll(latlngList)
                        .width(10f)
                        .color(activity!!.getColor(R.color.blackAlpha))
                        .startCap(ButtCap())
                        .endCap(ButtCap())
        )
    }

    fun addMarkers(){
        val result = (activity!! as ShowDateActivity).planResults
        var markerOptions = MarkerOptions()
        var boundsBuilder = LatLngBounds.builder()

        val bitmapDraw = ContextCompat.getDrawable(activity!!.applicationContext,R.drawable.marker) as BitmapDrawable
        val b = bitmapDraw.bitmap
        val marker = Bitmap.createScaledBitmap(b, 71, 100, false)

        for(i in 0 until result.size)
        {
            markerOptions
                    .position(latlngList[i])
                    .title(result[i]!!.name)
                    .icon(BitmapDescriptorFactory.fromBitmap(marker))
            if(result[i]!!.hour != null){
                val minute = result[i]!!.minute.toString()
                var str = result[i]!!.hour.toString()
                if(minute.length == 1)
                    str += "시 0" + minute + "분"
                else
                    str += "시 " + minute + "분"
                markerOptions.snippet(str)

            }
            else{
                markerOptions.snippet(null)
            }
            gMap.addMarker(markerOptions)

            boundsBuilder.include(latlngList[i])
        }
        if(result.isNotEmpty()){
            var bounds = boundsBuilder.build()
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100))
        }
    }
}



package konkukSW.MP2019.roadline.UI.date


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R


class Fragment4 : androidx.fragment.app.Fragment(), OnMapReadyCallback {
    var ListID = "init"
    var DayNum = 0;
    var spotList: ArrayList<Plan> = arrayListOf()
    var latlngList: ArrayList<LatLng> = arrayListOf()
    val mapFragment by lazy {
        this.childFragmentManager.findFragmentById(konkukSW.MP2019.roadline.R.id.mapView) as SupportMapFragment
    }
    lateinit var gMap: GoogleMap

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(
                konkukSW.MP2019.roadline.R.layout.fragment_fragment4,
                container,
                false
        )
        setObserve()
        initData()
        mapFragment.getMapAsync(this)
        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        refresh()
        super.onHiddenChanged(hidden)
    }

    fun refresh() {
//        val ft = fragmentManager!!.beginTransaction()
//        ft.detach(this).attach(this).commit()
        spotList.clear()
        latlngList.clear()
        mapFragment.getMapAsync(this)
        initData()
    }

    fun setObserve() {
//        (activity!! as ShowDateActivity).planResultsData.observe(viewLifecycleOwner, Observer {
//            setList()
//            mapFragment.getMapAsync(this)
//        })
        (activity!! as ShowDateActivity).planResults.addChangeListener { t, changeSet ->
            setList()
            mapFragment.getMapAsync(this)
        }
    }

    fun setList() {
        spotList.clear()
        latlngList.clear()
        for (T_Plan in (activity!! as ShowDateActivity).planResults) {
            spotList.add(
                    Plan(
                            T_Plan.listID,
                            T_Plan.dayNum,
                            T_Plan.id,
                            T_Plan.name,
                            T_Plan.locationX,
                            T_Plan.locationY,
                            T_Plan.hour.toString() + ":" + T_Plan.minute.toString(),
                            T_Plan.memo,
                            T_Plan.pos,
                            -1,
                            false
                    )
            )
            latlngList.add(LatLng(T_Plan.locationY, T_Plan.locationX))
        }
    }

    fun initData() {
        if (activity != null) {
            val intent = activity!!.intent
            if (intent != null) {
                ListID = intent.getStringExtra("ListID")
                DayNum = intent.getIntExtra("DayNum", 0)
                Log.v("logtag", ListID + DayNum.toString())
            }
        }
        setList()

    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        gMap.clear()
        addPolylines()
        addMarkers()
    }

    fun addPolylines() {
        val polyLine = gMap.addPolyline(
                PolylineOptions()
                        .addAll(latlngList)
                        .width(10f)
                        .color(activity!!.getColor(R.color.blackAlpha))
                        .startCap(ButtCap())
                        .endCap(ButtCap())
        )
    }

    fun addMarkers() {
        var markerOptions = MarkerOptions()
        var boundsBuilder = LatLngBounds.builder()

        val text = TextView(context)
        text.text = ""
        val generator = IconGenerator(context)
        generator.setBackground(context!!.getDrawable(R.drawable.marker))
        generator.setContentView(text)
//        val icon = generator.makeIcon()
        val icon = Bitmap.createScaledBitmap(generator.makeIcon(), 71, 100, false)
//
//        val bitmapDraw = ContextCompat.getDrawable(
//                activity!!.applicationContext,
//                R.drawable.marker
//        ) as BitmapDrawable
//        val b = bitmapDraw.bitmap
//        val marker = Bitmap.createScaledBitmap(b, 71, 100, false)

        var markerList: ArrayList<Marker> = ArrayList()
        for (i in 0..spotList.size - 1) {
            markerOptions
                    .position(latlngList[i])
                    .title(spotList[i].name)
                    .snippet(spotList[i].time)
                    .icon(BitmapDescriptorFactory.fromBitmap(icon))
            markerList.add(gMap.addMarker(markerOptions))
            boundsBuilder.include(latlngList[i])
        }
        gMap.setOnMarkerClickListener {
            val center = CameraUpdateFactory.newLatLng(it.position)
            gMap.animateCamera(center)

            for(i in markerList){
                i.showInfoWindow()
            }
            true
        }
        if (spotList.isNotEmpty()) {
            var bounds = boundsBuilder.build()
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }
}



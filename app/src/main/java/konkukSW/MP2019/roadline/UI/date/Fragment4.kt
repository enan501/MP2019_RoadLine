package konkukSW.MP2019.roadline.UI.date


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R


class Fragment4 : Fragment(),OnMapReadyCallback {
    var spotList:ArrayList<Plan> = arrayListOf(
        Plan("",0,"","건국대학교",37.540005, 127.076530,
            "14:30","그린호프ㄱ", 0),
        Plan("",0,"","개미집2",37.545200,  127.076277,
            "12:30","ㅎㅇ", 0),
        Plan("",0,"","신천역 4번출구",37.511429, 127.084784,
            "11:30","ㅂㅇ", 0)
    )
    var latlngList:ArrayList<LatLng> = arrayListOf(
        LatLng(37.540005, 127.076530),
        LatLng(37.545200, 127.076277),
        LatLng(37.511429, 127.084784)
    )
    lateinit var gMap: GoogleMap
    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        addPolylines()
        addMarkers()
    }
    fun addPolylines(){
        val polyLine = gMap.addPolyline(
            PolylineOptions()
                .add(LatLng(37.540005, 127.076530), LatLng(37.545200, 127.076277),LatLng(37.511429, 127.084784))
                .width(15f)
                .color(R.color.colorPrimary)
                .startCap(ButtCap())
                .endCap(ButtCap())
        )
    }
    fun addMarkers(){
        var markerOptions = MarkerOptions()
        var boundsBuilder = LatLngBounds.builder()

        for(i in 0..spotList.size-1)
        {
            markerOptions
                .position(latlngList[i])
                .title(spotList[i].name)
                .snippet(spotList[i].time)
            gMap.addMarker(markerOptions)

            boundsBuilder.include(latlngList[i])
        }
        var bounds = boundsBuilder.build()
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100))
    }

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view: View = inflater!!.inflate(konkukSW.MP2019.roadline.R.layout.fragment_fragment4, container, false)
    val mapFragment = this.childFragmentManager.findFragmentById(konkukSW.MP2019.roadline.R.id.mapView) as SupportMapFragment
    mapFragment.getMapAsync(this)
    return view
    }

}

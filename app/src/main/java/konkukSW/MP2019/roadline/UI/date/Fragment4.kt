package konkukSW.MP2019.roadline.UI.date


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import konkukSW.MP2019.roadline.Data.Dataclass.Spot


class Fragment4 : Fragment(),OnMapReadyCallback {
    var spotList:ArrayList<Spot> = arrayListOf(
        Spot("건국대학교","14:30","그린호프ㄱ"),
        Spot("개미집2","20:30","밥술ㄱ"),
        Spot("신천역4번출구","21:30","걷다보니 앞이야"))
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
                .width(20f)
                .color(0x60aebff)
                .startCap(ButtCap())
                .endCap(ButtCap())
            //.clickable(true)
        )
    }
    fun addMarkers(){
        //TODO("마커 추가안하면 진짜 개똥클 선 그려짐")
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

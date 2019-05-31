package konkukSW.MP2019.roadline.UI.date


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import konkukSW.MP2019.roadline.R


class Fragment4 : Fragment(),OnMapReadyCallback {
    lateinit var gMap: GoogleMap
    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
    }

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view: View = inflater!!.inflate(R.layout.fragment_fragment4, container, false)
    val mapFragment = this.childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
    mapFragment.getMapAsync(this)
    return view
}

}

package konkukSW.MP2019.roadline.UI.date


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import konkukSW.MP2019.roadline.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class Fragment4 : androidx.fragment.app.Fragment(), OnMapReadyCallback {
    var ListID = "init"
    var DayNum = 0;
    var latlngList: ArrayList<LatLng> = arrayListOf()
    var isReady = false

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
        init()
        return view
    }
    fun init(){
        setObserve()
        initData()
        setList()
        mapFragment.getMapAsync(this)
    }

    fun setObserve() {
        (requireActivity() as ShowDateActivity).planResults.addChangeListener { _, _ ->
            setList()
            mapFragment.getMapAsync(this)
        }
    }

    fun setList() {
        latlngList.clear()
        for (T_Plan in (requireActivity() as ShowDateActivity).planResults) {
            latlngList.add(LatLng(T_Plan.locationY, T_Plan.locationX))
        }
    }

    fun initData() {
        if (activity != null) {
            val intent = requireActivity().intent
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
        isReady = true
    }

    fun addPolylines() {
        gMap.addPolyline(
                PolylineOptions()
                        .addAll(latlngList)
                        .width(10f)
                        .color(requireActivity().getColor(R.color.blackAlpha))
                        .startCap(ButtCap())
                        .endCap(ButtCap())
        )
    }

    fun addMarkers(){
        val result = (requireActivity() as ShowDateActivity).planResults
        var markerOptions = MarkerOptions()
        var boundsBuilder = LatLngBounds.builder()

        val bitmapDraw = ContextCompat.getDrawable(requireActivity().applicationContext,R.drawable.marker) as BitmapDrawable
        val b = bitmapDraw.bitmap
        val marker = Bitmap.createScaledBitmap(b, 71, 100, false)

        for(i in 0 until result.size) {
            var title = ""
            if(result[i]!!.nameAlter == null){
                title = result[i]!!.name
            }
            else{
                title = result[i]!!.nameAlter!!
            }
            var time: String? = null
            if(result[i]!!.hour != null){
                val minute = result[i]!!.minute.toString()
                time = result[i]!!.hour.toString()
                if(minute.length == 1)
                    time += "시 0" + minute + "분"
                else
                    time += "시 " + minute + "분"
            }
            markerOptions
                    .position(latlngList[i])
                    .title(title)
                    .snippet(time)
                    .icon(BitmapDescriptorFactory.fromBitmap(marker))

            gMap.addMarker(markerOptions)

            boundsBuilder.include(latlngList[i])
        }
        if(result.isNotEmpty()){
            var bounds = boundsBuilder.build()
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100))
        }
    }

    fun getScreenShot(){
        if(isReady){
            val callback = GoogleMap.SnapshotReadyCallback{
                val bitmap = it
                if(bitmap != null){
                    val storage = context!!.cacheDir
                    val fileName = "temp.jpg"
                    val tempFile = File(storage, fileName)
                    try {
                        tempFile.createNewFile()
                        val out = FileOutputStream(tempFile)
                        bitmap!!.compress(Bitmap.CompressFormat.JPEG,100, out)
                        out.close()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    Log.v("tag", tempFile.toURI().toString())
                    var uri = FileProvider.getUriForFile(context!!,context!!.packageName + ".fileprovider",tempFile)
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.addCategory(Intent.CATEGORY_DEFAULT)
                    shareIntent.type = "image/*"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(shareIntent, "여행 일정 공유"))
                }
            }
            gMap.snapshot(callback)
        }
    }
}



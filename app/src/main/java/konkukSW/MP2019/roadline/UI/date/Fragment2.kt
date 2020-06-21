package konkukSW.MP2019.roadline.UI.date


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.collection.LruCache
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import konkukSW.MP2019.roadline.Data.Adapter.PlanGridAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan


class Fragment2 : androidx.fragment.app.Fragment() {

    var ListID = "";
    var DayNum = 0;
    var LOCATION_REQUEST = 1234
    lateinit var planAdapter:PlanGridAdapter
    lateinit var gpsCheck : CheckBox

    lateinit var v:View
    lateinit var timelineView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(konkukSW.MP2019.roadline.R.layout.fragment_fragment2, container, false)
        Log.d("mytag", "fragment2 : oncreateview")
        setObserve()
        initData()
        initLayout()
        addListener()
        return v
    }

//    fun refresh(){
////        val ft = fragmentManager!!.beginTransaction()
////        ft.detach(this).attach(this).commit()
//    }



    fun setObserve(){
        (activity!! as ShowDateActivity).planResults.addChangeListener{t, changeSet->
            gpsCheck.isChecked = false
            planAdapter.notifyDataSetChanged()
            if(t.size == 0){
                gpsCheck.visibility = View.GONE
            }
            else{
                gpsCheck.visibility = View.VISIBLE
            }
//            val modifications: Array<OrderedCollectionChangeSet.Range> = changeSet.changeRanges
//            for (range in modifications) {
//                Log.d("mytag", "itemCHange: " + range.startIndex.toString() +" " + range.length.toString())
//                val rPos = convertPos(range.startIndex)
//                if(range.length == 1){ //수정
//                    planAdapter.notifyItemRangeChanged(rPos, range.length)
//                }
//                else{ //이동
//                    var nPos = -1
//                    if(range.startIndex % 10 in 0..4){
//                        nPos = (range.startIndex / 10) * 10
//                    }
//                    else{
//                        nPos = 5 + (range.startIndex / 10) * 10
//                    }
//                    Log.d("mytag", "itemChangeMove : " + nPos.toString() + " " + (rPos + range.length -1).toString())
//                    planAdapter.notifyItemRangeChanged(nPos, rPos + range.length - nPos)
//                }
////                planAdapter.notifyItemRangeRemoved(range.startIndex, range.length)
////                planAdapter.notifyItemChanged(rPos)
//            }
//
//            val insertions = changeSet.insertionRanges
//            for (range in insertions) {
//                Log.d("mytag", "itemInsert: " + range.startIndex.toString() +" " + range.length.toString())
//                val rPos = convertPos(range.startIndex)
//                if(range.startIndex % 10 in 0..4){
//                    if(rPos % 10 == 0){
//                        if(rPos - 5 >= 0)
//                            planAdapter.notifyItemChanged(rPos - 5)
//                    }
//                    else{
//                        planAdapter.notifyItemChanged(rPos - 1)
//                    }
//                    planAdapter.notifyItemInserted(rPos)
//                }
//                else if(range.startIndex % 10 == 5){
//                    planAdapter.notifyItemChanged(range.startIndex - 1)
//                    planAdapter.notifyItemRangeInserted(rPos, 5)
//                }
//                else {
//                    planAdapter.notifyItemChanged(rPos + 1)
//                    planAdapter.notifyItemChanged(rPos)
//                }
//            }
        }
    }

//    fun convertPos(position:Int) : Int{
//        when(position % 10){
//            5 -> return position + 4
//            6 -> return position + 2
//            8 -> return position - 2
//            9 -> return position - 4
//            else -> return position
//        }
//    }

    fun initData(){
        if(activity != null){
            val intent = activity!!.intent
            if(intent != null){
                ListID = intent.getStringExtra("ListID")
                DayNum = intent.getIntExtra("DayNum", 0)
            }
        }

    }


    fun initLayout(){
        timelineView = v.findViewById(konkukSW.MP2019.roadline.R.id.timeline_recycleView) as RecyclerView
        val layoutManager = GridLayoutManager(activity!!, 5)
        timelineView.layoutManager = layoutManager
        planAdapter = PlanGridAdapter((activity!! as ShowDateActivity).planResults, context!!)
        timelineView.adapter = planAdapter

        gpsCheck = v.findViewById(konkukSW.MP2019.roadline.R.id.gps_check)
        if((activity!! as ShowDateActivity).planResults.size == 0){
            gpsCheck.visibility = View.GONE
        }
        else{
            gpsCheck.visibility = View.VISIBLE
        }
    }


    fun human(){
        if(checkAppPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val providers = lm!!.getProviders(true)
            var location: android.location.Location? = null
            for (provider in providers) {
                val l = lm.getLastKnownLocation(provider) ?: continue
                if (location == null || l.accuracy < location.accuracy) {
                    // Found best last known location: %s", l);
                    location = l
                }
            }
            planAdapter.setHuman(location)
        }
        else{
            initPermission()
        }
    }

    fun addListener() {
        planAdapter.itemClickListener = object : PlanGridAdapter.OnItemClickListener{
            override fun OnItemClick(holder: PlanGridAdapter.ViewHolder, view: View, data: T_Plan, position: Int) {
                Log.d("mytag", "Click")
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("planId", data.id)
                i.putExtra("DayNum", DayNum)
                i.putExtra("ListID", ListID)
                i.putExtra("path", 1)
                i.putExtra("pos", position)
                startActivityForResult(i, 123)
                //showAddSpot()
            }
        }

        gpsCheck.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                human()
            else{
                val beforeHumanIndex = planAdapter.humanIndex
                planAdapter.humanIndex = -1
                planAdapter.notifyItemChanged(beforeHumanIndex)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 애드스팟 하고나서 돌아왔을때 어댑터뷰 바로 갱신
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123) {
            if(resultCode == Activity.RESULT_OK) {
//                refresh()
            }
        }
    }
    fun checkAppPermission(requestPermission: Array<String>): Boolean {
        val requestResult = BooleanArray(requestPermission.size)
        for (i in requestResult.indices) {
            requestResult[i] = ContextCompat.checkSelfPermission(
                    context!!,
                    requestPermission[i]
            ) == PackageManager.PERMISSION_GRANTED
            if (!requestResult[i]) {
                return false
            }
        }
        return true
    } // checkAppPermission
    fun askPermission(requestPermission: Array<String>, REQ_PERMISSION: Int) {
        requestPermissions(
                requestPermission,
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
                    Toast.makeText(context,"승인되었습니다",Toast.LENGTH_SHORT).show()
                    human()
                } else {
                    Toast.makeText(context,"거부됨",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun initPermission(){
        if(checkAppPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))) {
        }
        else{
            val builder = AlertDialog.Builder(context!!)
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

    fun getScreenshotFromRecyclerView(): Bitmap? {
        var bigBitmap: Bitmap? = null
        val size = planAdapter.itemCount
        if(size == 0)
            return null
        var height = 0
        val paint = Paint()
        var iHeight = 0
        var width = 0
        var iWidth = 0
        var rheight = 0
        var rwidth = 0
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Use 1/8th of the available memory for this memory cache.
        val cacheSize = maxMemory / 8
        val bitmaCache = LruCache<String, Bitmap>(cacheSize)
        var count:Int = 0
        for (i in 0 until size) {
            val holder = planAdapter.createViewHolder(timelineView, planAdapter.getItemViewType(i))
            planAdapter.onBindViewHolder(holder, i)
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(timelineView.width, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(timelineView.height, View.MeasureSpec.UNSPECIFIED)
            )
            holder.itemView.layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)
            holder.itemView.isDrawingCacheEnabled = true
            holder.itemView.buildDrawingCache()
            val drawingCache = holder.itemView.drawingCache
            if (drawingCache != null) {
                bitmaCache.put(i.toString(), drawingCache)
            }
            else{
                Log.d("mytag", "null : " + i.toString())
            }
            if(count % 5 == 0){
                height += holder.itemView.measuredHeight
                width = holder.itemView.measuredWidth
            }else{
                width += holder.itemView.measuredWidth
            }
            count++
            rheight = holder.itemView.measuredHeight
            rwidth = holder.itemView.measuredWidth
        }

        bigBitmap = Bitmap.createBitmap(rwidth*5, height, Bitmap.Config.ARGB_8888)
        val bigCanvas = Canvas(bigBitmap!!)
        bigCanvas.drawColor(Color.WHITE)

        for (i in 0 until size) {
            val bitmap = bitmaCache.get(i.toString())
            iHeight = (i/5)*rheight
            iWidth = (i%5)*rwidth
            Log.d("mytag", bitmap.toString())
            bigCanvas.drawBitmap(bitmap!!, iWidth.toFloat(), iHeight.toFloat(), paint)
            bitmap!!.recycle()
        }
        return bigBitmap
    }

}

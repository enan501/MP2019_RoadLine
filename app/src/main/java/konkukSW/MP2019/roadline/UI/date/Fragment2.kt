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
import konkukSW.MP2019.roadline.UI.widget.BaseDialog
import kotlinx.android.synthetic.main.row_plan.*


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
        (activity!! as ShowDateActivity).planResults.addChangeListener{t, _->
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
    override fun onDestroy() {
        (activity!! as ShowDateActivity).planResults.removeAllChangeListeners()
        super.onDestroy()
    }

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
            val builder = BaseDialog.Builder(context!!).create()
            builder.setTitle("알림")
                    .setMessage("GPS 권한을 허용할까요?")
                    .setCancelButton("확인", View.OnClickListener {
                        askPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST)
                    })
                    .show()
        }
    }

    fun getScreenshotFromRecyclerView(): Bitmap? {
        var bigBitmap: Bitmap?
        val size = planAdapter.itemCount
        if(size == 0)
            return null
        var height = 0
        var bitmapList = ArrayList<Bitmap>()


        var childWidth = 0
        var childHeight = 0
        for(i in 0 until size){
            val childView = timelineView.getChildAt(i)
            if(childView != null){
                childWidth = childView.width
                childHeight = childView.height
            }
            break
        }

        for (i in 0 until size) {
            val holder = planAdapter.createViewHolder(timelineView, planAdapter.getItemViewType(i))
            planAdapter.onBindViewHolder(holder, i)
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.EXACTLY)
            )
            holder.itemView.layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)
            val buffer = Bitmap.createBitmap(holder.itemView.measuredWidth, holder.itemView.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(buffer)
            holder.itemView.draw(canvas)
            bitmapList.add(buffer)
            if(i % 5 == 0){
                height += holder.itemView.measuredHeight
            }
        }

        bigBitmap = Bitmap.createBitmap(timelineView.width, height, Bitmap.Config.ARGB_8888)
        val bigCanvas = Canvas(bigBitmap!!)

        val paint = Paint()
        for (i in 0 until size) {
            val bitmap = bitmapList[i]
            val iHeight = (i / 5) * childHeight
            val iWidth = (i % 5) * childWidth
            bigCanvas.drawBitmap(bitmap, iWidth.toFloat(), iHeight.toFloat(), paint)
            bitmap.recycle()
        }
        return bigBitmap
    }
}

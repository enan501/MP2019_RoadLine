package konkukSW.MP2019.roadline.UI.date


import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import konkukSW.MP2019.roadline.Data.Adapter.DateIconListAdapter
import konkukSW.MP2019.roadline.Data.Adapter.DateItemTouchHelperCallback
import konkukSW.MP2019.roadline.Data.Adapter.PlanListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.fragment_fragment1.*

class Fragment1 : androidx.fragment.app.Fragment() {  //리스트

    lateinit var planAdapter: PlanListAdapter
    lateinit var iconAdapter: DateIconListAdapter
    lateinit var rView: RecyclerView
    lateinit var rIconView: RecyclerView
    lateinit var v:View
    lateinit var itemTouchHelper:ItemTouchHelper
    lateinit var callback: DateItemTouchHelperCallback


    companion object{
        var mode = 1 //0->수정모드, 1->평상시, 2->캡처할때
    }

    var listID = ""
    var dayNum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_fragment1, container, false)
        init()
        return v
    }

    override fun onDestroy() {
        (activity!! as ShowDateActivity).planResults.removeAllChangeListeners()
        super.onDestroy()
    }

    fun init(){
        setObserve()
        initData()
        initLayout()
        addListener()
        initSwipe()
    }
    fun setObserve(){
        (activity!! as ShowDateActivity).planResults.addChangeListener { _, _->
            iconAdapter = DateIconListAdapter(planAdapter.itemCount - 1, context!!)
            rIconView.adapter = iconAdapter
        }
    }
    fun initData(){
        mode = 1
        if(activity != null){
            val intent = activity!!.intent
            if(intent != null){
                listID = intent.getStringExtra("ListID")
                dayNum = intent.getIntExtra("DayNum", 0)
            }
        }
    }

    fun initLayout(){
        rView = v.findViewById(R.id.f1_rView) as androidx.recyclerview.widget.RecyclerView
        val layoutManager = LinearLayoutManager(
                activity!!,
                RecyclerView.VERTICAL,
                false
        )
        rView.layoutManager = layoutManager
//        adapter = DateListAdapter(planList, context!!)
        planAdapter = PlanListAdapter((activity!! as ShowDateActivity).planResults, context!!)
        rView.adapter = planAdapter


        rIconView = v.findViewById(R.id.f1_rViewIcon) as androidx.recyclerview.widget.RecyclerView
        val layoutManager2 = LinearLayoutManager(
                activity!!,
                RecyclerView.VERTICAL,
                false
        )
        rIconView.layoutManager = layoutManager2
        iconAdapter = DateIconListAdapter(planAdapter.itemCount - 1, context!!)
        rIconView.adapter = iconAdapter
    }

    fun addListener(){
        planAdapter.itemClickListener = object : PlanListAdapter.OnItemClickListener{
            override fun OnItemClick(holder: PlanListAdapter.FooterViewHolder) {
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("pos", planAdapter.itemCount - 1)
                i.putExtra("DayNum", dayNum)
                i.putExtra("ListID", listID)
                startActivityForResult(i,123)
            }

            override fun OnItemClick(holder: PlanListAdapter.ItemViewHolder, view: View, data: T_Plan, position: Int) {
                if(mode == 1){
                    val i = Intent(activity, AddSpotActivity::class.java)
                    i.putExtra("DayNum", dayNum)
                    i.putExtra("ListID", listID)
                    i.putExtra("pos", position)
                    i.putExtra("planId", data.id)
                    startActivityForResult(i, 123)
                }
                else if(mode == 0){ //수정 모드
                    for(i in 0 until planAdapter.itemCount - 1){
                        val anim = ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, 100.0f, 0.0f)
                        anim.duration = 300
                        val view = rView.findViewHolderForAdapterPosition(i)
                        if(view is PlanListAdapter.ItemViewHolder){
                            view.deleteBtn.visibility = View.INVISIBLE
                            view.dragBtn.startAnimation(anim)
                            view.dragBtn.visibility = View.VISIBLE
                        }
                    }
                    mode = 1
                }
            }
        }

        planAdapter.itemDragListener = object :PlanListAdapter.OnItemDragListener{
            override fun onStartDrag(holder: RecyclerView.ViewHolder) {
                Log.d("mytag", "startdrag")
                planAdapter.onDetachedFromRecyclerView(f1_rView)
                itemTouchHelper.startDrag(holder)
            }
        }

        planAdapter.itemLongClickListener = object : PlanListAdapter.OnItemLongClickListener{
            override fun onItemLongClick() {
                if(mode == 1){
                    val anim = ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, 100.0f, 0.0f)
                    anim.duration = 300
                    for(i in 0 until planAdapter.itemCount - 1){
                        val view = rView.findViewHolderForAdapterPosition(i)
                        if(view is PlanListAdapter.ItemViewHolder){
                            view.deleteBtn.visibility = View.VISIBLE
                            view.dragBtn.visibility = View.INVISIBLE
                            view.deleteBtn.startAnimation(anim)
                        }
                    }
                    mode = 0
                }
            }
        }

        planAdapter.itemChangeListener = object :PlanListAdapter.OnItemChangeListener{
            override fun onItemChange() {

            }
        }

    }

    fun initSwipe(){
        callback = DateItemTouchHelperCallback(planAdapter, activity!!, ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rView) //recyclerView에 붙이기
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123) {
            if(resultCode == Activity.RESULT_OK) {
//                refresh()
            }
        }
    }


    fun combineImage(first:Bitmap, second:Bitmap):Bitmap?{
        val result = Bitmap.createBitmap(first.width + second.width, first.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        canvas.drawBitmap(first, 0.0f, 0.0f, paint)
        canvas.drawBitmap(second, first.width.toFloat(), 0.0f, paint)
        return result
    }

    fun getScreenshot() : Bitmap?{
        var result: Bitmap? = null
        if(planAdapter.itemCount > 1){
            val first = getScreenshotFromRecyclerView(rIconView, iconAdapter)
            val second = getScreenshotFromRecyclerView(rView, planAdapter)
            result = combineImage(first!!, second!!)
        }
        return result
    }

    fun getScreenshotFromRecyclerView(view: RecyclerView, ad: RecyclerView.Adapter<RecyclerView.ViewHolder>): Bitmap? {
        mode = 2
        var bigBitmap: Bitmap?
        val size = ad.itemCount - 1
        if(size == 0)
            return null
        var height = 0
        var iHeight = 0
        val paint = Paint()
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        val cacheSize = maxMemory / 8
        val bitmapCache = LruCache<String, Bitmap>(cacheSize)
        for (i in 0 until size) {
            val holder = ad.createViewHolder(view, ad.getItemViewType(i))
            ad.onBindViewHolder(holder, i)
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            holder.itemView.layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)
            holder.itemView.isDrawingCacheEnabled = true
            holder.itemView.buildDrawingCache()
            val drawingCache = holder.itemView.drawingCache
            if (drawingCache != null) {
                bitmapCache.put(i.toString(), drawingCache)
            }
            height += holder.itemView.measuredHeight
        }

        bigBitmap = Bitmap.createBitmap(view.measuredWidth, height, Bitmap.Config.ARGB_8888)
        val bigCanvas = Canvas(bigBitmap!!)
        bigCanvas.drawColor(Color.WHITE)

        for (i in 0 until size) {
            val bitmap = bitmapCache.get(i.toString())
            bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
            iHeight += bitmap.getHeight()
            bitmap.recycle()
        }
        mode = 1
        return bigBitmap
    }
}

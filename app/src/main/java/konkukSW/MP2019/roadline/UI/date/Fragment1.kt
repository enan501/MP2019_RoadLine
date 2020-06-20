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
    private val TYPE_ONE  = -2
    private val TYPE_START = -4
    private val TYPE_END = -3
    //private val TYPE_MID = -1

//    var planList:ArrayList<Plan> = arrayListOf()
//    lateinit var adapter:DateListAdapter
    lateinit var planAdapter: PlanListAdapter
    lateinit var iconAdapter: DateIconListAdapter
    lateinit var rView: RecyclerView
    lateinit var rIconView: RecyclerView
    lateinit var v:View
    lateinit var itemTouchHelper:ItemTouchHelper
    lateinit var callback: DateItemTouchHelperCallback


    companion object{
        var editMode = false
    }

    var listID = ""
    var dayNum = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fragment1, container, false)
        Log.d("mytest", "oncreateview")
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
        editMode = false
        if(activity != null){
            val intent = activity!!.intent
            if(intent != null){
                listID = intent.getStringExtra("ListID")
                dayNum = intent.getIntExtra("DayNum", 0)
            }
        }
//        setList()
    }
//    fun setList(){
//        planList.clear()
//        for(T_Plan in (activity!! as ShowDateActivity).planResults){
//            planList.add(Plan(T_Plan.listID, T_Plan.dayNum, T_Plan.id, T_Plan.name,
//                    T_Plan.locationX, T_Plan.locationY, T_Plan.hour.toString() + ":" + T_Plan.minute.toString(), T_Plan.memo, T_Plan.pos, -1, false))
//        }
//        if(planList.size == 1)
//            planList.get(0).viewType = TYPE_ONE
//        else if(planList.size > 1){
//            planList.get(0).viewType = TYPE_START
//            planList.get(planList.size - 1).viewType = TYPE_END
//        }
//    }
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
                if(!editMode){
                    val i = Intent(activity, AddSpotActivity::class.java)
                    i.putExtra("DayNum", dayNum)
                    i.putExtra("ListID", listID)
                    i.putExtra("pos", position)
                    i.putExtra("planId", data.id)
                    startActivityForResult(i, 123)
                }
                else{
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
                    editMode = false
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
                if(!editMode){
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
                    editMode = true
                }
            }
        }

        planAdapter.itemChangeListener = object :PlanListAdapter.OnItemChangeListener{
            override fun onItemChange() {

            }
        }
//        adapter.itemClickListener = object : DateListAdapter.OnItemClickListener{
//            //addBtn 클릭했을 때
//            override fun OnItemClick(holder: DateListAdapter.FooterViewHolder) {
//                val i = Intent(activity, AddSpotActivity::class.java)
//                i.putExtra("pos", planList.size)
//                i.putExtra("DayNum", dayNum)
//                i.putExtra("ListID", listID)
//                startActivityForResult(i,123)
//            }
//
//            //리사이클러뷰 아이템 클릭했을 때
//            override fun OnItemClick(holder: DateListAdapter.ItemViewHolder, view: View, data: Plan, position: Int) {
//                Log.d("mytest", "click")
//                if(!editMode){
//                    val i = Intent(activity, AddSpotActivity::class.java)
//                    i.putExtra("spot", data)
//                    i.putExtra("DayNum", dayNum)
//                    i.putExtra("ListID", listID)
//                    i.putExtra("path", 1)
//                    i.putExtra("pos", position)
//                    i.putExtra("planId", data.id)
//                    startActivityForResult(i, 123)
//                }
//                else{
//                    for(i in 0..adapter.itemCount){
//                        val anim = ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, 100.0f, 0.0f)
//                        anim.duration = 300
//                        val view = rView.findViewHolderForAdapterPosition(i)
//                        if(view is DateListAdapter.ItemViewHolder){
//                            view.deleteBtn.visibility = View.INVISIBLE
//                            view.dragBtn.startAnimation(anim)
//                            view.dragBtn.visibility = View.VISIBLE
//                        }
//                    }
//                    editMode = false
//                }
//            }
//        }
//
//        adapter.itemDragListener = object : DateListAdapter.OnItemDragListener {
//            override fun onStartDrag(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
//                itemTouchHelper.startDrag(holder)
//            }
//        }
//        adapter.itemLongClickListener = object : DateListAdapter.OnItemLongClickListener {
//            override fun onItemLongClick() {
//                Log.d("mytest", "longclick" + editMode.toString())
//                if(!editMode){
//                    val anim = ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, 100.0f, 0.0f)
//                    anim.duration = 300
//                    for(i in 0..adapter.itemCount){
//                        val view = rView.findViewHolderForAdapterPosition(i)
//                        if(view is DateListAdapter.ItemViewHolder){
//                            view.deleteBtn.visibility = View.VISIBLE
//                            view.dragBtn.visibility = View.INVISIBLE
//                            view.deleteBtn.startAnimation(anim)
//                        }
//                    }
//                    editMode = true
//                }
//            }
//        }
//
//        adapter.itemChangeListener = object :DateListAdapter.OnItemChangeListener{
//            override fun onItemChange() {
//                Log.d("mytest", "planlist size(delete) : " + planList.size.toString())
//                iconAdapter = DateIconListAdapter(planList.size, context!!)
//                rIconView.adapter = iconAdapter
//            }
//        }
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

//    fun refresh() {
////        val ft = fragmentManager!!.beginTransaction()
////        ft.detach(this).attach(this).commit()
//    }

    fun combineImage(first:Bitmap, second:Bitmap):Bitmap?{
        val result = Bitmap.createBitmap(first.width + second.width, first.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        canvas.drawBitmap(first, 0.0f, 0.0f, paint)
        canvas.drawBitmap(second, first.width.toFloat(), 0.0f, paint)
        return result
    }

    fun getScreenshot() : Bitmap?{
        val first = getScreenshotFromRecyclerView(rIconView, iconAdapter)
        val second = getScreenshotFromRecyclerView(rView, planAdapter)
        val result = combineImage(first!!, second!!)
        return result
    }

    fun getScreenshotFromRecyclerView(view: RecyclerView, ad: RecyclerView.Adapter<RecyclerView.ViewHolder>): Bitmap? {

        var bigBitmap: Bitmap? = null
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

        return bigBitmap
    }
}

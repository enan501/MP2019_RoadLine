package konkukSW.MP2019.roadline.UI.date


import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.Adapter
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.DateIconListAdapter
import konkukSW.MP2019.roadline.Data.Adapter.DateItemTouchHelperCallback
import konkukSW.MP2019.roadline.Data.Adapter.DateListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.row_spot.*

class Fragment1 : androidx.fragment.app.Fragment() {  //리스트
    private val TYPE_ONE  = -2
    private val TYPE_START = -4
    private val TYPE_END = -3
    //private val TYPE_MID = -1

    lateinit var planList:ArrayList<Plan>
    lateinit var adapter:DateListAdapter
    lateinit var iconAdapter: DateIconListAdapter
    lateinit var rView: androidx.recyclerview.widget.RecyclerView
    lateinit var rIconView: androidx.recyclerview.widget.RecyclerView
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

    fun init(){
        initData()
        initLayout()
        addListener()
        initSwipe()
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
        Realm.init(context!!)
        val realm = Realm.getDefaultInstance()
        val results:RealmResults<T_Plan> = realm.where<T_Plan>(T_Plan::class.java)
            .equalTo("listID", listID)
            .equalTo("dayNum", dayNum)
            .findAll()
            .sort("pos")
        planList = ArrayList()
        for(T_Plan in results){
            planList.add(Plan(T_Plan.listID, T_Plan.dayNum, T_Plan.id, T_Plan.name,
                T_Plan.locationX, T_Plan.locationY, T_Plan.time, T_Plan.memo, T_Plan.pos, -1, false))
        }
        if(planList.size == 1)
            planList.get(0).viewType = TYPE_ONE
        else if(planList.size > 1){
            planList.get(0).viewType = TYPE_START
            planList.get(planList.size - 1).viewType = TYPE_END
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
        adapter = DateListAdapter(planList, context!!)
        rView.adapter = adapter
        rIconView = v.findViewById(R.id.f1_rViewIcon) as androidx.recyclerview.widget.RecyclerView
        val layoutManager2 = LinearLayoutManager(
                activity!!,
                RecyclerView.VERTICAL,
                false
        )
        rIconView.layoutManager = layoutManager2
        iconAdapter = DateIconListAdapter(planList.size, context!!)
        rIconView.adapter = iconAdapter
    }

    fun addListener(){
        adapter.itemClickListener = object : DateListAdapter.OnItemClickListener{
            //addBtn 클릭했을 때
            override fun OnItemClick(holder: DateListAdapter.FooterViewHolder) {
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("pos", planList.size)
                i.putExtra("DayNum", dayNum)
                i.putExtra("ListID", listID)
                startActivityForResult(i,123)
            }

            //리사이클러뷰 아이템 클릭했을 때
            override fun OnItemClick(holder: DateListAdapter.ItemViewHolder, view: View, data: Plan, position: Int) {
                Log.d("mytest", "click")
                if(!editMode){
                    val i = Intent(activity, AddSpotActivity::class.java)
                    i.putExtra("spot", data)
                    i.putExtra("DayNum", dayNum)
                    i.putExtra("ListID", listID)
                    i.putExtra("path", 1)
                    i.putExtra("pos", position)
                    startActivityForResult(i, 123)
                }
                else{
                    for(i in 0..adapter.itemCount){
                        val anim = ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, 100.0f, 0.0f)
                        anim.duration = 300
                        val view = rView.findViewHolderForAdapterPosition(i)
                        if(view is DateListAdapter.ItemViewHolder){
                            view.deleteBtn.startAnimation(anim)
                            view.dragBtn.startAnimation(anim)
                            view.deleteBtn.visibility = View.GONE
                            view.dragBtn.visibility = View.GONE
                        }
                    }
                    editMode = false
                }
            }
        }

        adapter.itemDragListener = object : DateListAdapter.OnItemDragListener {
            override fun onStartDrag(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(holder)
            }
        }
        adapter.itemLongClickListener = object : DateListAdapter.OnItemLongClickListener {
            override fun onItemLongClick() {
                Log.d("mytest", "longclick" + editMode.toString())
                if(!editMode){
                    val anim = ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, 100.0f, 0.0f)
                    anim.duration = 300
                    for(i in 0..adapter.itemCount){
                        val view = rView.findViewHolderForAdapterPosition(i)
                        if(view is DateListAdapter.ItemViewHolder){
                            view.deleteBtn.visibility = View.VISIBLE
                            view.dragBtn.visibility = View.VISIBLE
                            view.deleteBtn.startAnimation(anim)
                            view.dragBtn.startAnimation(anim)
                        }
                    }
                    editMode = true
                }
            }
        }

        adapter.itemChangeListener = object :DateListAdapter.OnItemChangeListener{
            override fun onItemChange() {
                Log.d("mytest", "planlist size(delete) : " + planList.size.toString())
                iconAdapter = DateIconListAdapter(planList.size, context!!)
                rIconView.adapter = iconAdapter
            }
        }
    }

    fun initSwipe(){
        callback = DateItemTouchHelperCallback(adapter, activity!!, ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rView) //recyclerView에 붙이기
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123) {
            if(resultCode == Activity.RESULT_OK) {
                refresh()
            }
        }
    }

    fun refresh() {
        val ft = fragmentManager!!.beginTransaction()
        ft.detach(this).attach(this).commit()
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
        val first = getScreenshotFromRecyclerView(rIconView, iconAdapter)
        val second = getScreenshotFromRecyclerView(rView, adapter)
        val result = combineImage(first!!, second!!)
        return result
    }

    fun getScreenshotFromRecyclerView(view: androidx.recyclerview.widget.RecyclerView, ad: androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>): Bitmap? {
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

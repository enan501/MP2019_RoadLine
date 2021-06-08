package konkukSW.MP2019.roadline.UI.date


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    lateinit var backView: TextView

    private val MODE_EDIT = 0
    private val MODE_DEFAULT = 1
    private val MODE_CAPTURE = 2

    var mode = MODE_DEFAULT
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
        (requireActivity() as ShowDateActivity).planResults.removeAllChangeListeners()
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
        (requireActivity() as ShowDateActivity).planResults.addChangeListener { _, _->
            iconAdapter = DateIconListAdapter(planAdapter.itemCount - 1, requireContext())
            rIconView.adapter = iconAdapter
            if(planAdapter.itemCount == 1){
                backView.visibility = View.VISIBLE
            }
            else{
                backView.visibility = View.INVISIBLE
            }
        }
        Log.d("fragment1","hi")
    }
    fun initData(){
        mode = MODE_DEFAULT
        if(activity != null){
            val intent = requireActivity().intent
            if(intent != null){
                listID = intent.getStringExtra("ListID")
                dayNum = intent.getIntExtra("DayNum", 0)
            }
        }
    }

    fun initLayout(){
        rView = v.findViewById(R.id.f1_rView) as androidx.recyclerview.widget.RecyclerView


        planAdapter = PlanListAdapter((requireActivity() as ShowDateActivity).planResults, requireContext(), this)
        rView.adapter = planAdapter


        rIconView = v.findViewById(R.id.f1_rViewIcon) as androidx.recyclerview.widget.RecyclerView

        iconAdapter = DateIconListAdapter(planAdapter.itemCount - 1, requireContext())
        rIconView.adapter = iconAdapter
        backView = v.findViewById(R.id.backView)
        if(planAdapter.itemCount == 1){
            backView.visibility = View.VISIBLE
        }
        else{
            backView.visibility = View.INVISIBLE
        }
    }

    fun addListener(){
        planAdapter.itemClickListener = object : PlanListAdapter.OnItemClickListener {
            override fun OnItemClick(holder: PlanListAdapter.FooterViewHolder) {
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("pos", planAdapter.itemCount - 1)
                i.putExtra("DayNum", dayNum)
                i.putExtra("ListID", listID)
                startActivityForResult(i,123)
            }

            override fun OnItemClick(holder: PlanListAdapter.ItemViewHolder, view: View, data: T_Plan, position: Int) {
                if(mode == MODE_DEFAULT){
                    val i = Intent(activity, AddSpotActivity::class.java)
                    i.putExtra("DayNum", dayNum)
                    i.putExtra("ListID", listID)
                    i.putExtra("pos", position)
                    i.putExtra("planId", data.id)
                    startActivityForResult(i, 123)
                }
                else if(mode == MODE_EDIT){ //수정 모드
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
                    mode = MODE_DEFAULT
                }
            }
        }

        planAdapter.itemDragListener = object :PlanListAdapter.OnItemDragListener{
            override fun onStartDrag(holder: RecyclerView.ViewHolder) {
                planAdapter.onDetachedFromRecyclerView(f1_rView)
                itemTouchHelper.startDrag(holder)
            }
        }

        planAdapter.itemLongClickListener = object : PlanListAdapter.OnItemLongClickListener{
            override fun onItemLongClick() {
                if(mode == MODE_DEFAULT){
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
                    mode = MODE_EDIT
                }
            }
        }

        planAdapter.itemChangeListener = object :PlanListAdapter.OnItemChangeListener{
            override fun onItemChange() {

            }
        }

    }

    fun initSwipe(){
        callback = DateItemTouchHelperCallback(planAdapter, requireActivity(), ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rView) //recyclerView에 붙이기
    }


    private fun combineImage(first:Bitmap, second:Bitmap):Bitmap?{
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

    private fun getScreenshotFromRecyclerView(view: RecyclerView, ad: RecyclerView.Adapter<RecyclerView.ViewHolder>): Bitmap? {
        mode = MODE_CAPTURE
        var bigBitmap: Bitmap?
        val size = ad.itemCount - 1
        if(size == 0)
            return null
        var height = 0
        var iHeight = 0

        var bitmapList = ArrayList<Bitmap>()

        var childHeight = 0
        for(i in 0 until size){
            val childView = view.getChildAt(i)
            if(childView != null){
                childHeight = childView.height
            }
            break
        }

        for (i in 0 until size) {
            val holder = ad.createViewHolder(view, ad.getItemViewType(i))
            ad.onBindViewHolder(holder, i)
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.EXACTLY)
            )
            holder.itemView.layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)
            val buffer = Bitmap.createBitmap(holder.itemView.measuredWidth, holder.itemView.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(buffer)
            holder.itemView.draw(canvas)
            bitmapList.add(buffer)
            height += holder.itemView.measuredHeight
        }

        bigBitmap = Bitmap.createBitmap(view.width, height, Bitmap.Config.ARGB_8888)
        var bigCanvas = Canvas(bigBitmap!!)
        val paint = Paint()
        bigCanvas.drawColor(Color.WHITE)


        for (i in 0 until size) {
            val bitmap = bitmapList[i]
            bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
            iHeight += bitmap.height
            bitmap.recycle()
        }
        mode = MODE_DEFAULT
        return bigBitmap
    }
}

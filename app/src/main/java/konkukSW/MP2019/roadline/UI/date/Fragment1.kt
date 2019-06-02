package konkukSW.MP2019.roadline.UI.date


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import konkukSW.MP2019.roadline.Data.Adapter.DateItemTouchHelperCallback
import konkukSW.MP2019.roadline.Data.Adapter.DateListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.Plan


import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.fragment_fragment1.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment1 : Fragment(), DateListAdapter.ItemDragListener {  //리스트

    lateinit var planList:ArrayList<Plan>
    lateinit var adapter:DateListAdapter
    lateinit var rView:RecyclerView
    lateinit var v:View
    lateinit var itemTouchHelper:ItemTouchHelper

    var listId = ""
    var dayNum = 0

    companion object {
        fun newFragment(listId:String, dayNum:Int):Fragment1{
            val frag = Fragment1()
            frag.listId = listId
            frag.dayNum = dayNum
            return frag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fragment1, container, false)
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
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        val results:RealmResults<T_Plan> = realm.where<T_Plan>(T_Plan::class.java).equalTo("listID", listId).equalTo("dayNum", dayNum).findAll().sort("pos")
        planList = ArrayList<Plan>()
        for(T_Plan in results){
            planList.add(Plan(T_Plan.listID, T_Plan.dayNum, T_Plan.id, T_Plan.name, T_Plan.locationX, T_Plan.locationY, T_Plan.time, T_Plan.memo, T_Plan.pos, -1))
        }
        Log.v("size", planList.size.toString())
        Log.v("size", listId.toString())
        Log.v("size", dayNum.toString())
    }

    fun initLayout(){
        rView = v!!.findViewById(R.id.f1_rView) as RecyclerView
        val layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rView.layoutManager = layoutManager
        adapter = DateListAdapter(planList, this, context!!)
        rView.adapter = adapter

    }

    fun addListener(){
        adapter.itemClickListener = object : DateListAdapter.OnItemClickListener{
            //addBtn 클릭했을 때
            override fun OnItemClick(holder: DateListAdapter.FooterViewHolder) {
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("pos", planList.size)
                i.putExtra("listId", listId)
                i.putExtra("dayNum", dayNum)
                startActivityForResult(i,123)
            }

            //리사이클러뷰 아이템 클릭했을 때
            override fun OnItemClick(holder: DateListAdapter.ItemViewHolder, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("spot", data)
                startActivityForResult(i, 123)
            }
        }


    }

    override fun onStartDrag(holder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(holder)

    }


    fun initSwipe(){
        itemTouchHelper = ItemTouchHelper(DateItemTouchHelperCallback(adapter, activity!!, ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT))
        itemTouchHelper.attachToRecyclerView(rView) //recyclerView에 붙이기
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 애드스팟 하고나서 돌아왔을때 어댑터뷰 바로 갱신
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                planList.clear()
                listId = data!!.getStringExtra("listId")
                dayNum = data!!.getIntExtra("dayNum", -1)
                init() // 갱신
            }
        }
    }
}

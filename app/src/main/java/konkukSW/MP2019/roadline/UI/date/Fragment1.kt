package konkukSW.MP2019.roadline.UI.date


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
        val results:RealmResults<T_Plan> = realm.where<T_Plan>(T_Plan::class.java).findAll()
        planList = ArrayList<Plan>()
        for(T_Plan in results){
            planList.add(Plan(T_Plan.listID, T_Plan.dayNum, T_Plan.id, T_Plan.name, T_Plan.locationX, T_Plan.locationY, T_Plan.time, T_Plan.memo, -1))
        }
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
                startActivity(i)
            }

            //리사이클러뷰 아이템 클릭했을 때
            override fun OnItemClick(holder: DateListAdapter.ItemViewHolder, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val i = Intent(activity, AddSpotActivity::class.java)
                i.putExtra("spot", data)
//                val bundle = Bundle()
//                bundle.putSerializable("spot", data)
                startActivity(i)
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
}

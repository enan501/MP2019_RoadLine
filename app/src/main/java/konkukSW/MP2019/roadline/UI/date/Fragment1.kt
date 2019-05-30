package konkukSW.MP2019.roadline.UI.date


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import konkukSW.MP2019.roadline.Data.Adapter.DateItemTouchHelperCallback
import konkukSW.MP2019.roadline.Data.Adapter.DateListAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.Spot


import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.fragment_fragment1.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Fragment1 : Fragment(), DateListAdapter.ItemDragListener {  //리스트

    lateinit var spotList:ArrayList<Spot>
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
        spotList = ArrayList<Spot>()
        spotList.add(Spot("타이페이 역", "time", "memo"))
        spotList.add(Spot("스펀", "time", "memo"))
        spotList.add(Spot("허운트 마을", "time", "memo"))

    }

    fun initLayout(){
        rView = v!!.findViewById(R.id.f1_rView) as RecyclerView
        val layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        rView.layoutManager = layoutManager
        adapter = DateListAdapter(spotList, this)
        rView.adapter = adapter
    }

    fun addListener(){
        val addBtn = v!!.findViewById(R.id.f1_addBtn) as ImageView
        addBtn.setOnClickListener {
            val i = Intent(activity, AddSpotActivity::class.java)
            startActivity(i)
        }
        adapter.itemClickListener = object : DateListAdapter.OnItemClickListener{
            override fun OnItemClick(holder: DateListAdapter.ViewHolder, view: View, data: Spot, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                val i = Intent(activity, AddSpotActivity::class.java)
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

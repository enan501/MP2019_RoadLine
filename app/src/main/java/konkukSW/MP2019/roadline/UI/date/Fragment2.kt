package konkukSW.MP2019.roadline.UI.date


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.MoneyItemAdapter
import konkukSW.MP2019.roadline.Data.Adapter.PlanAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.MoneyItem
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_show_money.*
import kotlinx.android.synthetic.main.fragment_fragment2.*

var StartedFlag = false;

class Fragment2 : Fragment() {

    var ListID = "";
    var DayNum = 0;

    lateinit var adapter: PlanAdapter
    var data : ArrayList<Plan> = ArrayList()

    var ViewTypeArray:Array<Int> = arrayOf(
        2,
        0,0,0,5,6,0,0,0,7,8,
        0,0,0,5,6,0,0,0,7,8,
        0,0,0,5,6,0,0,0,7,8,
        0,0,0,5,6,0,0,0,7,8,
        0,0,0,5,6,0,0,0,7,8,
        0,0,0,5,6,0,0,0,7,8,
        0,0,0,5,6,0,0,0,7,8,
        0,0,0,5,6,0,0,0,7,8)
    var position = 0;
    var lastPosition = 0; // 제일 마지막에 추가된 일정
    var foldCount = 0; // 5가되면 foldFlag가 바뀐다.
    var foldFlag = false; // false : 오른쪽으로 추가 모드, true : 왼쪽으로 추가모드

    lateinit var v:View
    lateinit var timelineView :RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_fragment2, container, false)

        init()
        return v
    }


    fun addListener() {
        adapter.itemClickListener = object : PlanAdapter.OnItemClickListener {
            override fun OnItemClick(holder: PlanAdapter.ViewHolder0, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")
            }
            override fun OnItemClick(holder: PlanAdapter.ViewHolder1, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder2, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder3, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder4, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder5, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder6, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder7, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder8, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder9, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder10, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                System.out.println("zzzzzzzzz")

            }
        }
    }
    fun init()
    {
        if(StartedFlag == true) {
            data.clear()
            position = 0;
            foldFlag = false;
            foldCount = 0;
        }
        StartedFlag = true
        // 이거 안하면 계속 중복되서 아이템 추가됨

        if(activity != null){
            val intent = activity!!.intent
            if(intent != null){
                ListID = intent.getStringExtra("ListID")
                DayNum = intent.getIntExtra("DayNum", 0)
            }
        }

        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        val q: RealmResults<T_Plan> = realm.where<T_Plan>(T_Plan::class.java)
            .equalTo("listID", ListID)
            .equalTo("dayNum", DayNum)
            .findAll()
            .sort("pos")

        timelineView = v!!.findViewById(R.id.timeline_recycleView) as RecyclerView
        val layoutManager = GridLayoutManager(activity!!, 5)
        timelineView.layoutManager = layoutManager
        adapter = PlanAdapter(data)
        timelineView.adapter = adapter

        for(i in 0..q.size-1)
        {
            addItem(ListID, DayNum, q.get(i)!!.id, q.get(i)!!.name, q.get(i)!!.locationX, q.get(i)!!.locationY,
                q.get(i)!!.time, q.get(i)!!.memo)
        }

        if(data.size > 1) {
            // 마지막 일정 모양 바꿔주기
            if (foldFlag == true) {
                if (foldCount == 0)
                    data.get(lastPosition).viewType = 1
                else if (foldCount == 1)
                    data.get(lastPosition).viewType = 3
                else
                    data.get(lastPosition).viewType = 2

            } else {
                if (foldCount == 0)
                    data.get(lastPosition).viewType = 2
                else if (foldCount == 1)
                    data.get(lastPosition).viewType = 4
                else
                    data.get(lastPosition).viewType = 1
            }
        }
        else if(data.size == 1)
        {
            data.get(lastPosition).viewType = 10
        }

        adapter.notifyDataSetChanged()
        addListener()

    }
    fun addItem(listID:String, DayNum:Int, id:String, name:String, locaX:Double, locaY:Double, time:String, memo:String)
    {
        if(foldFlag == false) { // 오른쪽으로 추가
            data.add(position, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position]))
            lastPosition = position;
        }
        else // 왼쪽으로 추가
        {
            if(foldCount == 0) {
                data.add(position, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0,9))
                data.add(position + 1, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0,9))
                data.add(position + 2, Plan(listID, DayNum, id, name, locaX, locaY, time, memo,0,9))
                data.add(position + 3, Plan(listID, DayNum, id, name, locaX, locaY, time, memo,0,9))
                data.add(position + 4, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position]))
                lastPosition = position + 4;
            }
            else if(foldCount == 1) {
                data.removeAt(position+2)
                data.add(position + 2, Plan(listID, DayNum, id, name, locaX, locaY, time,memo, 0, ViewTypeArray[position]))
                lastPosition = position + 2;
            }
            else if(foldCount == 2) {
                data.removeAt(position)
                data.add(position, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position]))
                lastPosition = position;
            }
            else if(foldCount == 3) {
                data.removeAt(position-2)
                data.add(position-2, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position]))
                lastPosition = position - 2;
            }
            else if(foldCount == 4) {
                data.removeAt(position-4)
                data.add(position-4, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position]))
                lastPosition = position - 4;
            }
        }
        position++
        foldCount++
        if (foldCount == 5)
        {
            foldCount = 0;
            if(foldFlag == true)
                foldFlag = false
            else
                foldFlag = true
        }
    }

}

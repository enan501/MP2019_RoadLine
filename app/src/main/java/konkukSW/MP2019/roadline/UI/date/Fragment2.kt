package konkukSW.MP2019.roadline.UI.date


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import konkukSW.MP2019.roadline.Data.Adapter.PlanAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.Plan
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.fragment_fragment2.*

var StartedFlag = false;

class Fragment2 : Fragment() {

    var ListID = "aaa"; // 이건 나중에 디비에서 받아와야함
    var DayNum = 0; // 이건 나중에 디비에서 받아와야함

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var ll = inflater.inflate(R.layout.fragment_fragment2, container, false)
        return ll
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(StartedFlag == true) {
            data.clear()
            position = 0;
            foldFlag = false;
            foldCount = 0;
        }
        StartedFlag = true
        // 이거 안하면 계속 중복되서 아이템 추가됨

        val layoutManager = GridLayoutManager(getActivity(), 5)
        timeline_recycleView.layoutManager = layoutManager
        adapter = PlanAdapter(data)
        timeline_recycleView.adapter = adapter

        addItem(ListID, DayNum, "a1", "건국대학교", 0.0, 0.0, "NULL", "NULL")
        addItem(ListID, DayNum, "a2", "건국대학교", 0.0, 0.0, "NULL", "NULL")
        addItem(ListID, DayNum, "a3", "건국대학교", 0.0, 0.0, "NULL", "NULL")
        addItem(ListID, DayNum, "a4", "건국대학교", 0.0, 0.0, "NULL", "NULL")
        addItem(ListID, DayNum, "a5", "건국대학교", 0.0, 0.0, "NULL", "NULL")


        // 마지막 일정 모양 바꿔주기
        if(foldFlag == true) {
            if(foldCount == 0)
                data.get(lastPosition).viewType = 1
            else if(foldCount == 1)
                data.get(lastPosition).viewType = 3
            else
                data.get(lastPosition).viewType = 2

        } else {
            if(foldCount == 0)
                data.get(lastPosition).viewType = 2
            else if(foldCount == 1)
                data.get(lastPosition).viewType = 4
            else
                data.get(lastPosition).viewType = 1
        }
        adapter.notifyDataSetChanged()
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

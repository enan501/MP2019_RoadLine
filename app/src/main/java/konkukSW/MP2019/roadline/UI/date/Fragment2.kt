package konkukSW.MP2019.roadline.UI.date


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.Adapter.PlanAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Plan
import konkukSW.MP2019.roadline.Data.Dataclass.Plan



var StartedFlag = false;

class Fragment2 : Fragment() {

    var ListID = "";
    var DayNum = 0;
    var LOCATION_REQUEST = 1234
    lateinit var adapter: PlanAdapter
    var data : ArrayList<Plan> = ArrayList()
    lateinit var gpsCheck : CheckBox
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
        v = inflater.inflate(konkukSW.MP2019.roadline.R.layout.fragment_fragment2, container, false)

        init()
        return v
    }
    fun human(){
        if(checkAppPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))) {

            val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val providers = lm!!.getProviders(true)
            var location: android.location.Location? = null
            for (provider in providers) {
                val l = lm!!.getLastKnownLocation(provider) ?: continue
                if (location == null || l.getAccuracy() < location!!.getAccuracy()) {
                    // Found best last known location: %s", l);
                    location = l
                }
            }
            val clat = location!!.latitude
            val clng = location!!.longitude
            var latDif = data[0].locationY - clat
            var lngDif = data[0].locationX - clng
            var dif = latDif*latDif + lngDif*lngDif
            var min = dif
            var minIndex = 0
            data[0].humanFlag = false
            for(i in 1..data.size-1){
                latDif = data[i].locationY - clat
                lngDif = data[i].locationX - clng
                data[i].humanFlag = false
                dif = latDif*latDif+lngDif*lngDif
                if(min>dif){
                    min = dif
                    minIndex = i
                }
            }
            data[minIndex].humanFlag = true
            adapter.notifyDataSetChanged()

        }
        else{
            initPermission()
        }
    }

    fun addListener() {
        adapter.itemClickListener = object : PlanAdapter.OnItemClickListener {
            override fun OnItemClick(holder: PlanAdapter.ViewHolder0, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)
            }
            override fun OnItemClick(holder: PlanAdapter.ViewHolder1, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder2, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder3, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder4, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder5, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder6, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder7, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder8, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder9, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }

            override fun OnItemClick(holder: PlanAdapter.ViewHolder10, view: View, data: Plan, position: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                showAddSpot(data)

            }
        }
        gpsCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                human()
            else{
                for(plan in data){
                    plan.humanFlag = false
                }
                adapter.notifyDataSetChanged()
            }

        }
    }

    fun showAddSpot(data:Plan)
    {
        val i = Intent(activity, AddSpotActivity::class.java)
        i.putExtra("spot", data)
        i.putExtra("DayNum", DayNum)
        i.putExtra("ListID", ListID)
        i.putExtra("path", 1)
        startActivityForResult(i, 123)
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

        timelineView = v!!.findViewById(konkukSW.MP2019.roadline.R.id.timeline_recycleView) as RecyclerView
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

        gpsCheck = v!!.findViewById<CheckBox>(konkukSW.MP2019.roadline.R.id.gps_check)
        adapter.notifyDataSetChanged()
        addListener()

        if(data.size == 0)
            gpsCheck.visibility = View.GONE
        else
            gpsCheck.visibility = View.VISIBLE

    }
    fun addItem(listID:String, DayNum:Int, id:String, name:String, locaX:Double, locaY:Double, time:String, memo:String)
    {
        if(foldFlag == false) { // 오른쪽으로 추가
            data.add(position, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position], false))
            lastPosition = position;
        }
        else // 왼쪽으로 추가
        {
            if(foldCount == 0) {
                data.add(position, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0,9, false))
                data.add(position + 1, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0,9, false))
                data.add(position + 2, Plan(listID, DayNum, id, name, locaX, locaY, time, memo,0,9, false))
                data.add(position + 3, Plan(listID, DayNum, id, name, locaX, locaY, time, memo,0,9, false))
                data.add(position + 4, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position], false))
                lastPosition = position + 4;
            }
            else if(foldCount == 1) {
                data.removeAt(position+2)
                data.add(position + 2, Plan(listID, DayNum, id, name, locaX, locaY, time,memo, 0, ViewTypeArray[position], false))
                lastPosition = position + 2;
            }
            else if(foldCount == 2) {
                data.removeAt(position)
                data.add(position, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position], false))
                lastPosition = position;
            }
            else if(foldCount == 3) {
                data.removeAt(position-2)
                data.add(position-2, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position], false))
                lastPosition = position - 2;
            }
            else if(foldCount == 4) {
                data.removeAt(position-4)
                data.add(position-4, Plan(listID, DayNum, id, name, locaX, locaY, time, memo, 0, ViewTypeArray[position], false))
                lastPosition = position - 4
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 애드스팟 하고나서 돌아왔을때 어댑터뷰 바로 갱신
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                val ft = fragmentManager!!.beginTransaction()
                ft.detach(this).attach(this).commit()

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
    fun askPermission(requestPermission: Array<String>,
                      REQ_PERMISSION: Int) {
        ActivityCompat.requestPermissions(
                activity!!, requestPermission,
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
                    Toast.makeText(context!!.applicationContext,"승인되었습니다",Toast.LENGTH_SHORT).show()
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
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage("GPS 권한을 허용할까요?")
                    .setTitle("권한 요청")
                    .setIcon(konkukSW.MP2019.roadline.R.drawable.notification_action_background)
                    .setPositiveButton("OK"){
                        _,_ ->
                        askPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST)
                    }
            val dialog = builder.create()
            dialog.show()


        }
    }

}

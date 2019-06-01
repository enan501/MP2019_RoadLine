package konkukSW.MP2019.roadline.UI

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.DatePicker
import android.widget.EditText
import io.realm.Realm
import io.realm.RealmConfiguration
import konkukSW.MP2019.roadline.Data.Adapter.MainListAdapter
import konkukSW.MP2019.roadline.Data.DB.T_Day
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.Dataclass.MainList
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.PickDateActivity
import kotlinx.android.synthetic.main.activity_main_list.*
import java.util.*

class MainListActivity : AppCompatActivity() {

    var MLArray:ArrayList<MainList> = arrayListOf(
        )
    lateinit var MLAdapter:MainListAdapter
    lateinit var realm: Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)
        init()
    }
    fun init(){
        initData()
        initLayout()

        Realm.init(this);
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build() // DB 테이블 수정시 자동으로 모든 인스턴스들 삭제모드
        Realm.setDefaultConfiguration(config) // 데이터베이스 옵션 설정해주는것 한번만 하면 됨.
    }
    fun initLayout(){
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        ML_rView.layoutManager = layoutManager
        MLAdapter = MainListAdapter(MLArray)
        ML_rView.adapter = MLAdapter
        addListener()
    }
    fun initData(){
        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build() // DB 테이블 수정시 자동으로 모든 인스턴스들 삭제모드
        Realm.setDefaultConfiguration(config) // 데이터베이스 옵션 설정해주는것 한번만 하면 됨.
        realm = Realm.getDefaultInstance()
        val results = realm.where<T_List>(T_List::class.java).findAll()
        for(T_List in results){
            MLArray.add(MainList(T_List.id,T_List.title,T_List.date,""))
        }
    }
    fun addListener() {
        MLAdapter.itemClickListener = object : MainListAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MainListAdapter.ViewHolder, data: MainList, position: Int) {
                val MLIntent = Intent(applicationContext,PickDateActivity::class.java)
                MLIntent.putExtra("ListID",data.id)
                startActivity(MLIntent)
            }
        }
        ML_addListBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
            val addListDialog = layoutInflater.inflate(R.layout.add_list_dialog, null)
            val addListTitle = addListDialog.findViewById<EditText>(R.id.AL_title)
            val addListDate = addListDialog.findViewById<DatePicker>(R.id.AL_date)
            builder.setView(addListDialog)
                .setPositiveButton("추가") { dialogInterface, i ->
                    realm.beginTransaction()
                    val newList:T_List = realm.createObject(T_List::class.java,UUID.randomUUID().toString())
                    newList.title = addListTitle.text.toString()
                    newList.date = addListDate.year.toString() +". "+ (addListDate.month+1).toString() +". "+ addListDate.dayOfMonth.toString()

                    val newDay: T_Day = realm.createObject(T_Day::class.java)
                    newDay.ListID = newList.id
                    newDay.date = newList.date
                    newDay.num = 1
                    realm.deleteAll()
                    realm.commitTransaction()
                    MLArray.add(MainList(newList.id,newList.title,newList.date,""))
                    MLAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
    }
}

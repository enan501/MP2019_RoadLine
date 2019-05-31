package konkukSW.MP2019.roadline.UI

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import konkukSW.MP2019.roadline.Data.Adapter.MainListAdapter
import konkukSW.MP2019.roadline.Data.Dataclass.MainList
import konkukSW.MP2019.roadline.R
import konkukSW.MP2019.roadline.UI.date.PickDateActivity
import kotlinx.android.synthetic.main.activity_main_list.*

class MainListActivity : AppCompatActivity() {

    var data:ArrayList<MainList> = arrayListOf(MainList("대만여행","2019.02.10 ~ 2019.02.14",""),
            MainList("일본여행","2019.02.10 ~ 2019.02.14",""),
            MainList("우주여행","2019.02.10 ~ 2019.02.14",""),
            MainList("집여행","2019.02.10 ~ 2019.02.14","")
        )
    lateinit var MLAdapter:MainListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)
        init()
    }
    fun init(){
        initLayout()

    }
    fun initLayout(){
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        ML_rView.layoutManager = layoutManager
        MLAdapter = MainListAdapter(data)
        ML_rView.adapter = MLAdapter
        addListener()
    }
    fun addListener() {
        MLAdapter.itemClickListener = object : MainListAdapter.OnItemClickListener {
            override fun OnItemClick(holder: MainListAdapter.ViewHolder, data: MainList, position: Int) {
                val MLIntent = Intent(applicationContext,PickDateActivity::class.java)
                //MLIntent.putExtra()나중에 DB에서 인덱스 받아와서 넘겨주기
                startActivity(MLIntent)
            }
        }
        ML_addListBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this) //alert 다이얼로그 builder 이용해서 다이얼로그 생성
            val addListDialog = layoutInflater.inflate(R.layout.add_list_dialog, null)
            val dialogText = addListDialog.findViewById<EditText>(R.id.AL_title)
            builder.setView(addListDialog)
                .setPositiveButton("추가") { dialogInterface, i ->
                    //db에 넣기 = dialogText.text.toString()

                }
                .setNegativeButton("취소") { dialogInterface, i ->
                }
                .show()
        }
    }
}

package konkukSW.MP2019.roadline.UI.photo

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import konkukSW.MP2019.roadline.Data.Adapter.ImageFragmentAdapter
import konkukSW.MP2019.roadline.Data.DB.T_List
import konkukSW.MP2019.roadline.Data.DB.T_Photo
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.activity_detail_photo.*

class DetailPhotoActivity : AppCompatActivity() {
    lateinit var realm:Realm
    var isAll = false
    var photoId = ""
    var listId = ""
    var dayNum = -1
    lateinit var photoResults: RealmResults<T_Photo>
    lateinit var fAdapter : ImageFragmentAdapter
    var selectedPos = -1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_photo)
        init()
    }

    fun init() {
        initData()
        initLayout()
        initListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_gallery, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home ->{
                finish()
            }
            R.id.first->{ //삭제하기
                val builder = AlertDialog.Builder(this@DetailPhotoActivity)
                builder.setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제") { _, _ ->
                            val fragment = fAdapter.getItem(viewPager.currentItem) as ImageFragment
                            realm.beginTransaction()
                            photoResults.where().equalTo("id", fragment.getPhotoId()).findFirst()!!.deleteFromRealm()
                            realm.commitTransaction()

                            fAdapter.removeItem(viewPager.currentItem)
                            if(fAdapter.count == 0){
                                finish()
                            }
                            else if(viewPager.currentItem >= fAdapter.count){
                                viewPager.currentItem = fAdapter.count - 1
                            }
                            else{
                                viewPager.currentItem = viewPager.currentItem + 1
                            }
                        }
                        .setNegativeButton("취소") { _, _ -> }
                val dialog = builder.create()
                dialog.show()
            }
            R.id.second->{ //대표사진
                realm.beginTransaction()
                var list = realm.where(T_List::class.java).equalTo("id", listId).findFirst()
                list!!.img = photoResults[selectedPos]!!.img
                realm.commitTransaction()
                val builder = AlertDialog.Builder(this@DetailPhotoActivity)
                    builder.setMessage("대표사진으로 설정되었습니다.")
                    builder.setPositiveButton("확인") { _, _ ->
                }.show()
            }
            else->{ }
        }
        return super.onOptionsItemSelected(item)
    }


    fun initData() {
        Realm.init(applicationContext)
        realm = Realm.getDefaultInstance()
        val i = intent
        photoId = i.getStringExtra("photoId")
        isAll = i.getBooleanExtra("isAll", false)
        listId = i.getStringExtra("listId")
        if(isAll){
            photoResults = realm.where(T_Photo::class.java).equalTo("listID", listId).findAll()!!.sort("dayNum", Sort.ASCENDING, "dateTime", Sort.ASCENDING)
        }
        else{
            dayNum = i.getIntExtra("dayNum", -1)
            photoResults = realm.where(T_Photo::class.java).equalTo("listID", listId).equalTo("dayNum", dayNum).findAll()!!.sort("dateTime")
        }

        fAdapter = ImageFragmentAdapter(supportFragmentManager)
        viewPager!!.offscreenPageLimit = 3

        for(i in photoResults.indices){
            val fragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString("imgSrc", photoResults[i]!!.img)
            bundle.putString("photoId", photoResults[i]!!.id)
            fragment.arguments = bundle
            fAdapter.addItem(fragment)
            if(photoResults[i]!!.id == photoId){
                selectedPos = i
            }
        }
        viewPager.adapter = fAdapter
        viewPager.currentItem = selectedPos
    }

    fun initLayout(){
        setSupportActionBar(adp_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        setDateView(photoResults[selectedPos]!!.dateTime)
        dayView.text = "Day" + photoResults[selectedPos]!!.dayNum

    }


    fun initListener(){
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
//                setDateView(photoResults[position]!!.dateTime)
                dayView.text = "Day" + photoResults[position]!!.dayNum
                selectedPos = position
                fAdapter.notifyDataSetChanged()
            }
        })
    }

//    fun setDateView(dateTime: Long){
//        if(android.os.Build.VERSION.SDK_INT >= 26) {
//            val dateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일").withLocale(Locale.forLanguageTag("ko"))
//            val dateTimeFormat = DateTimeFormatter.ofPattern("a HH시 mm분 ").withLocale(Locale.forLanguageTag("ko"))
//            val dateTime = LocalDateTime.ofEpochSecond(dateTime, 0, ZoneOffset.of("+09:00"))
//            dateView.text = dateFormat.format(dateTime)
//            dateTimeView.text = dateTimeFormat.format(dateTime)
//        }
//        else{
//            val dateFormat = org.threeten.bp.format.DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일").withLocale(Locale.forLanguageTag("ko"))
//            val dateTimeFormat = org.threeten.bp.format.DateTimeFormatter.ofPattern("a HH시 mm분 ").withLocale(Locale.forLanguageTag("ko"))
//            val dateTime = org.threeten.bp.LocalDateTime.ofEpochSecond(dateTime, 0, org.threeten.bp.ZoneOffset.of("+09:00"))
//            dateView.text = dateFormat.format(dateTime)
//            dateTimeView.text = dateTimeFormat.format(dateTime)
//        }
//    }


}
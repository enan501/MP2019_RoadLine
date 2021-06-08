package konkukSW.MP2019.roadline.UI

import android.app.AlertDialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.jakewharton.threetenabp.AndroidThreeTen
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.NetworkStatus
import konkukSW.MP2019.roadline.R
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import kotlin.coroutines.CoroutineContext


class SplashActivity : AppCompatActivity(){
    lateinit var realm:Realm
    lateinit var curResults:RealmResults<T_Currency>
    lateinit var dialog:AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(konkukSW.MP2019.roadline.R.layout.activity_splash)
        Realm.init(this)
        AndroidThreeTen.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build() // DB 테이블 수정시 자동으로 모든 인스턴스들 삭제모드
        Realm.setDefaultConfiguration(config) // 데이터베이스 옵션 설정해주는것 한번만 하면 됨.
        dialog = AlertDialog.Builder(this).create()
        dialog.apply{
            setMessage(getString(R.string.no_network_no_currency))
            setButton(AlertDialog.BUTTON_POSITIVE,"닫기") { _, _-> finish()}
            setOnDismissListener { finish() }
        }
        GlobalScope.launch{
            checkCurrency()
        }
        Thread.sleep(1500)

    }
    suspend fun checkCurrency(){
        realm = Realm.getDefaultInstance()
        curResults = realm.where(T_Currency::class.java).findAll()
        if(NetworkStatus.isNetworkConnected(this)){
            getCurrency()
        }
        else{
            if(curResults.size == 0){
                withContext(Dispatchers.Main){
                    dialog.show()
                }
            } else {
                goToMainListActivity()
            }
        }
    }
    fun getCurrency():Boolean { // True : delay 주기
//        realm.beginTransaction()
//        val newCurrency = realm.createObject(T_Currency::class.java, "KRW")
//        newCurrency.name = "KRW"
//        newCurrency.rate = 1.0
//        realm.commitTransaction()

        if (curResults.size < 0) {
            //환율정보 db 초기 세팅
            //이름, 코드, 기호 parsing
            Jsoup.connect("https://kr.fxexchangerate.com/currency-symbols.html").get().run {
                select(".fxtable tr:nth-of-type(n+2)").forEach { element ->
                    val curName = element.select("td:nth-child(1)>a").text() //이름
                    val curCode = element.select("td:nth-child(2)").text() //코드
                    val curSymbol =
                            element.select("td:nth-child(3)").text().split(' ')[0].split(',')[0] //기
                    if (curCode.isNotEmpty()) {
                        setCurrency(curCode, curName, curSymbol)
                    }
                }
            }
            setCurrency("LAK", "라오스 킵", "₭")
            setCurrency("LBP", "레바논 파운드", "ل.ل")
            setCurrency("LSL", "레소토 로티", "L")
            setCurrency("LRD", "라이베리아 달러", "$")
            setCurrency("LYD", "리비아 디나르", "ل.د")

            //이름 기준 환율 parsing
            Jsoup.connect("https://kr.fxexchangerate.com/currency-exchange-rates.html").get().run {
                for(i in 1..5){
                    select(".fxtable:nth-of-type(${i}) tr:nth-of-type(n+2)").forEach {element ->
                        var curName = element.select("td:nth-child(1)>a").text()
                        var curRate = element.select("td:nth-child(2)").text()
//                        Log.d("currency",curName)
                        realm.beginTransaction()
                        curResults.where().equalTo("name",curName).findFirst()!!.rate = curRate.toDouble()
                        realm.commitTransaction()
                    }
                }

            }
            startActivity(Intent(this@SplashActivity, MainListActivity::class.java))
            finish()
            return false
        } else {
            startActivity(Intent(this@SplashActivity, MainListActivity::class.java))
            finish()
            return true
        }
    }

    fun goToMainListActivity(){
        startActivity(Intent(this@SplashActivity, MainListActivity::class.java))
        finish()
    }

    fun setCurrency(code:String, name:String, symbol:String ){
        realm.beginTransaction()
        val newCurrency = realm.createObject(T_Currency::class.java, code)
        newCurrency.name = name
        newCurrency.symbol = symbol
        realm.commitTransaction()
    }
}


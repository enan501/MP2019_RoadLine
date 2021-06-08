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
            startActivity(Intent(this@SplashActivity, MainListActivity::class.java))
            finish()
        }
        else{
            if(curResults.size == 0){
                withContext(Dispatchers.Main){
                    dialog.show()
                }
            } else {
                startActivity(Intent(this@SplashActivity, MainListActivity::class.java))
                finish()
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
            //이름,코드,환율 parsing
            Jsoup.connect("https://kr.fxexchangerate.com/currency-exchange-rates.html").get().run {
                select("tbody >tr").forEach {element ->
                    var curName = element.select("td:nth-child(2)>a").text()
                    var curCode = element.select("td:nth-child(3)>a").text()
                    var curRate = element.select("td:nth-child(4)").text()

                    realm.beginTransaction()
                    val newCurrency = realm.createObject(T_Currency::class.java, curCode)
                    newCurrency.name = curName
                    newCurrency.rate = curRate.toDouble()
                    realm.commitTransaction()
                     //println(index.toString() + " : " + curName + "/" + curCode + "/" + curRate)
                }
            }
            //코드와 일치하는 기호 parsing
            Jsoup.connect("https://kr.fxexchangerate.com/currency-symbols.html").get().run {
                select("tbody >tr").forEach {element ->
                    var curCode = element.select("td:nth-child(2)").text()
                    var curSymbol = element.select("td:nth-child(3)").text()
                    if (curCode.isNotEmpty()) {
                        realm.beginTransaction()
                        curResults.where().equalTo("code",curCode).findFirst()!!.symbol = curSymbol
                        realm.commitTransaction()

                        //println(index.toString() + " : " + curCode + "/" + curSymbol)
                    }
                }
            }
            //기호가 없는 화폐는 코드를 기호로 대체

            for(T_currency in curResults){
                if(T_currency.symbol.isEmpty())
                {
                    realm.beginTransaction()
                    var code = T_currency.code
                    T_currency.symbol = code
                    realm.commitTransaction()
                }
            }
            return false
        } else {
            return true
        }
    }



}


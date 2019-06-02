package konkukSW.MP2019.roadline.UI

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class SplashActivity : AppCompatActivity() {

    lateinit var realm:Realm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build() // DB 테이블 수정시 자동으로 모든 인스턴스들 삭제모드
        Realm.setDefaultConfiguration(config) // 데이터베이스 옵션 설정해주는것 한번만 하면 됨.
        GlobalScope.launch(Dispatchers.Default){
            getCurrency()
        }

        Thread.sleep(2500)
        startActivity(Intent(this, MainListActivity::class.java))
        finish()
    }
    fun getCurrency() {
        realm = Realm.getDefaultInstance()
        println("size : " + realm.where(T_Currency::class.java).findAll().size.toString())
        if (realm.where(T_Currency::class.java).findAll().size < 142) { //중복으로 받아오지 않음
            Jsoup.connect("https://kr.fxexchangerate.com/currency-exchange-rates.html").get().run {
                select("tbody >tr").forEachIndexed { index, element ->
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
            Jsoup.connect("https://kr.fxexchangerate.com/currency-symbols.html").get().run {
                select("tbody >tr").forEachIndexed { index, element ->
                    var curCode = element.select("td:nth-child(2)").text()
                    var curSymbol = element.select("td:nth-child(3)").text()
                    if (curCode.isNotEmpty()) {
                        realm.beginTransaction()
                        realm.where(T_Currency::class.java).equalTo("code",curCode).findFirst()!!.symbol = curSymbol
                        realm.commitTransaction()

                        //println(index.toString() + " : " + curCode + "/" + curSymbol)
                    }
                }
            }
            realm.beginTransaction()
            var results = realm.where(T_Currency::class.java).findAll()
            for(T_currency in results){
                if(T_currency.symbol.isEmpty())
                    T_currency.symbol = T_currency.code
            }
            realm.commitTransaction()

        }
        //DB에 담긴 환율 리스트 출력
//        var results = realm.where(T_Currency::class.java).findAll()
//        for(T_currency in results) {
//            println(T_currency.name + " : " + T_currency.code + " : " + T_currency.symbol + " : " + T_currency.rate)
//        }
    }


}


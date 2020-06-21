package konkukSW.MP2019.roadline

import android.content.Context
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.lang.Exception
import java.util.concurrent.TimeoutException

fun refreshCurrency(context: Context) {
    if(NetworkStatus.isNetworkConnected(context)){
        try {
            GlobalScope.launch{
                Realm.init(context)
                var realm = Realm.getDefaultInstance()
                var curResults = realm.where(T_Currency::class.java).findAll()
                //이미 db 구성되어 있으면 환율정보만 업데이트
                Jsoup.connect("https://kr.fxexchangerate.com/currency-exchange-rates.html").get().run {
                    select("tbody >tr").forEach { element ->
                        var curCode = element.select("td:nth-child(3)>a").text()
                        var curRate = element.select("td:nth-child(4)").text()

                        realm.beginTransaction()
                        curResults.where().equalTo("code", curCode).findFirst()!!.rate = curRate.toDouble()
                        realm.commitTransaction()
                    }
                }
            }
        } catch (e:TimeoutException){
            showCheckNetworkDialog(context)
        }

    }
}

fun showCheckNetworkDialog(context:Context){
    var dialog = android.app.AlertDialog.Builder(context).create()
    dialog.apply{
        setMessage(context.getString(R.string.no_network))
        setButton(AlertDialog.BUTTON_POSITIVE,"닫기") { _, _-> }
    }.show()
}

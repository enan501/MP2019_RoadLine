package konkukSW.MP2019.roadline

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import konkukSW.MP2019.roadline.Data.DB.T_Currency
import konkukSW.MP2019.roadline.UI.widget.BaseDialog
import konkukSW.MP2019.roadline.UI.widget.ProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.Exception
import java.util.concurrent.TimeoutException

fun refreshCurrency(context: Context) {
    val pd = ProgressDialog.Builder(context).create()
    if(NetworkStatus.isNetworkConnected(context)){
        try {
            pd.show()
            GlobalScope.launch{
                Realm.init(context)
                var realm = Realm.getDefaultInstance()
                var curResults = realm.where(T_Currency::class.java).findAll()

                Jsoup.connect("https://kr.fxexchangerate.com/currency-exchange-rates.html").get().run {
                    for(i in 1..5){
                        select(".fxtable:nth-of-type(${i}) tr:nth-of-type(n+2)").forEach {element ->
                            var curName = element.select("td:nth-child(1)>a").text()
                            println(curName)
                            var curRate = element.select("td:nth-child(2)").text()
                            realm.beginTransaction()
                            curResults.where().equalTo("name",curName).findFirst()!!.rate = curRate.toDouble()
                            realm.commitTransaction()
                        }
                    }

                }
                pd.dismissDialog()
                withContext(Dispatchers.Main){
                    var dialog = BaseDialog.Builder(context).create()
                    dialog.setMessage(context.getString(R.string.successfully_refreshed_currency))
                            .setTitle("알림")
                            .setOkButton("확인", View.OnClickListener { dialog.dismissDialog()})
                    .show()
                }
            }

        } catch (e:TimeoutException){
            showCheckNetworkDialog(context)
            pd.dismissDialog()
        }
    }
    else{
        showCheckNetworkDialog(context)
    }
}

fun showCheckNetworkDialog(context:Context){
    var dialog = BaseDialog.Builder(context).create()
    dialog.setMessage(context.getString(R.string.no_network))
            .setOkButton("닫기", View.OnClickListener { dialog.dismissDialog()})
            .show()
}

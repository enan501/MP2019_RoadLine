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

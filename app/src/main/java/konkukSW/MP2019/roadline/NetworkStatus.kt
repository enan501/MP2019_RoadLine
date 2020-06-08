package konkukSW.MP2019.roadline

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkStatus {
    companion object{
        const val TYPE_WIFI = 1
        const val TYPE_MOBILE = 2
        const val TYPE_NOT_CONNECTED = 3

        fun getConnectivityStatus(context: Context):Int {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.getNetworkCapabilities(manager.activeNetwork)
            if(networkInfo != null){
                if(networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    return TYPE_WIFI
                else if(networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    return TYPE_MOBILE
            }
            return TYPE_NOT_CONNECTED
        }
    }
}
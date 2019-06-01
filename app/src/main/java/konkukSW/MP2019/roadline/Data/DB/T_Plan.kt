package konkukSW.MP2019.roadline.Data.DB

import com.google.android.gms.maps.model.LatLng
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_Plan : RealmObject() {

    var listID :String = "" // T_List의 id의 외래키
    var dayID: String = "" // T_Day의 id의 외래키

    var id : String = ""
    var name: String =  ""
    var locationX :Double = 0.0
    var locationY :Double = 0.0

    var time: String = ""
    var memo:String = ""
    var pos : Int = 0
}
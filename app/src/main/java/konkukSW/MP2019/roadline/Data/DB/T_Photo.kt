package konkukSW.MP2019.roadline.Data.DB

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class T_Photo : RealmObject(){

    var listID :String = "" // T_List의 id의 외래키
    var dayNum: Int = 0 // T_Day의 id의 외래키

    @PrimaryKey
    var id : String = ""
    var img: String = ""
    var dateTime: Long = 0
}
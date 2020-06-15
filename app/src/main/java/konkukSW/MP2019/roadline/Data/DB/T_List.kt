package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class T_List : RealmObject() {
    @PrimaryKey
    var id :String = ""

    var title: String = ""
//    var dateStart: Date = Date() //시작일
//    var dateEnd: Date = Date() //종료일
    var dateStart: Long = 0 //epochDay
    var dateEnd: Long = 0
//    var pos:Int = 0
    var img: String = ""
    var currencys:RealmList<T_Currency> = RealmList()
}
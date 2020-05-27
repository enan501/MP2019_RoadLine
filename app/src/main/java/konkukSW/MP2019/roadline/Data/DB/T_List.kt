package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_List : RealmObject() {
    @PrimaryKey
    var id :String = ""

    var title: String = ""
    var dateStart: String = "" //시작일
    var dateEnd:String = ""//종료일
    var pos:Int = 0
    var img: String = ""
    var currencys:RealmList<T_Currency> = RealmList()
}
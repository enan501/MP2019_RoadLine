package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_Currency : RealmObject() {
    @PrimaryKey
    var code:String = ""

    var name:String = ""
    var rate:Double = 0.0
    var symbol:String = ""


}
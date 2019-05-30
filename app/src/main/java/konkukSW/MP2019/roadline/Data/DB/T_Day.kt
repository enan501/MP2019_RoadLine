package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_Day : RealmObject() {

    var listNum :Int = 0 // T_List의 num의 외래키

    var num: Int = 0
    var date: String = ""

}
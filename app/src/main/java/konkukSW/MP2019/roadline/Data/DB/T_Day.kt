package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject

open class T_Day : RealmObject() {

    var ListID :String = "" // T_List의 id의 외래키

    var num: Int = 0
    var date: String = ""

}
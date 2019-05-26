package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_List : RealmObject() {
    @PrimaryKey
    var num :Int = 0

    var title: String = ""
    var date: String = ""

}
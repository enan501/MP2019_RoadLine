package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_List : RealmObject() {
    @PrimaryKey
    var id :String = ""

    var title: String = ""
    var date: String = ""
    var pos:Int = 0

}
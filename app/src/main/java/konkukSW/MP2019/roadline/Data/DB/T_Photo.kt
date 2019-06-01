package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_Photo : RealmObject() {

    var listID :String = "" // T_List의 id의 외래키
    var dayID: String = "" // T_Day의 id의 외래키

    var id : String = ""
    var img: String = ""
    var date: String = ""
    var pos : Int = 0

}
package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class T_Plan : RealmObject() {

    var listID :String = "" // T_List의 id의 외래키
    var dayNum: Int = 0 // T_Day의 id의 외래키

    @PrimaryKey
    var id : String = ""
    var name: String =  ""
    var nameAlter: String? = null
    var locationX :Double = 0.0
    var locationY :Double = 0.0

    var hour: Int? = null
    var minute: Int? = null
    var memo: String? = null

    var pos : Int = 0


}
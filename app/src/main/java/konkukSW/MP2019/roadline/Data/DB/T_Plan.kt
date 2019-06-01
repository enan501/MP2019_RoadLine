package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject

open class T_Plan : RealmObject() {

    var listID :String = "" // T_List의 id의 외래키
    var dayNum: Int = 0 // T_Day의 id의 외래키

    var id : String = ""
    var name: String =  ""
    var locationX :Double = 0.0
    var locationY :Double = 0.0

    var time: String = ""
    var memo:String = ""
    var pos : Int = 0
}
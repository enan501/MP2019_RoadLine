package konkukSW.MP2019.roadline.Data.DB

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class T_Plan : RealmObject() {

    var listNum :Int = 0 // T_List의 num의 외래키
    var dayNum: Int = 0 // T_Day의 num의 외래키

    var num : Int = 0
    var name: String = ""
    var locationX: Float = 0.0f
    var locationY: Float = 0.0f
    var time: String = ""

}
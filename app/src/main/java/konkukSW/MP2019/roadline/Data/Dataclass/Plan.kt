package konkukSW.MP2019.roadline.Data.Dataclass

import java.io.Serializable

data class Plan (var listNum:Int, var dayNum:Int, var num: Int, var name:String,
                 var locationX:Float, var locationY:Float, var time:String, var memo:String, var viewType:Int) :Serializable {
}

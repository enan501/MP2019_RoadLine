package konkukSW.MP2019.roadline.Data.Dataclass

import java.io.Serializable

data class Plan (var listID:String, var dayNum:Int, var id: String, var name:String,
                 var locationX:Float, var locationY:Float, var time:String, var memo:String, var viewType:Int) :Serializable {
}

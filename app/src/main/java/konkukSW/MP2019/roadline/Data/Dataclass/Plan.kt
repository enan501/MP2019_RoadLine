package konkukSW.MP2019.roadline.Data.Dataclass

import java.io.Serializable

data class Plan (var listID:String, var dayNum:Int, var id: String, var name:String,
                 var locationX:Double, var locationY:Double, var time:String, var memo:String?, var pos:Int, var viewType:Int,
                 var humanFlag:Boolean) :Serializable {
}

package konkukSW.MP2019.roadline.Data.Dataclass

import konkukSW.MP2019.roadline.Data.DB.T_Currency

data class MainList(var id:String, var title:String, var dateStart:String, var dateEnd:String, var image:String, var currencys: List<T_Currency>)
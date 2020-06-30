package konkukSW.MP2019.roadline.Extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.View

fun View.setVisible(isVisible:Boolean){
    if(isVisible)
        this.visibility = View.VISIBLE
    else
        this.visibility = View.GONE
}

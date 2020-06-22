package konkukSW.MP2019.roadline.Extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.getRotatedBitmap(degree: Int): Bitmap? {
    var bitmap = this
    if (degree != 0 && bitmap != null) {
        val matrix = Matrix()
        matrix.setRotate(
                degree.toFloat(),
                bitmap.width.toFloat() / 2,
                bitmap.height.toFloat() / 2
        )
        try {
            val tmpBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
            )
            if (bitmap != tmpBitmap) {
                bitmap.recycle()
                bitmap = tmpBitmap
            }
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
    }
    return bitmap
}
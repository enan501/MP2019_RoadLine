package konkukSW.MP2019.roadline.Extension

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import java.io.IOException
import java.io.InputStream


fun Uri.getPathFromUri(context: Context): String {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    cursor!!.moveToNext()
    val path = cursor.getString(cursor.getColumnIndex("_data"))
    cursor.close()
    return path
}

fun Uri.getExifOrientation(context: Context): Int {
    var exif: ExifInterface? = null
    try {
        exif = ExifInterface(context.contentResolver.openInputStream(this)!!)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    if (exif != null) {
        val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return 90
            ExifInterface.ORIENTATION_ROTATE_180 -> return 180
            ExifInterface.ORIENTATION_ROTATE_270 -> return 270
        }
    }
    return 0
}

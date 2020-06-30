package konkukSW.MP2019.roadline.UI.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.View
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.add_photo_dialog.*

open class AddPhotoDialog(context: Context) : Dialog(context) {

    open class Builder(val mContext: Context) {
        open val dialog = AddPhotoDialog(mContext)
        open fun create(): Builder {
            dialog.create()
            dialog.setContentView(R.layout.add_photo_dialog)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            var size = Point()
            dialog.window!!.windowManager.defaultDisplay.getSize(size)
            dialog.window!!.setLayout((size.x * 0.872f).toInt(), size.y)
            return this
        }

        open fun setCameraButton(onClick: View.OnClickListener): Builder {
            dialog.button1.setOnClickListener(onClick)
            return this
        }

        open fun setAlbumButton(onClick: View.OnClickListener): Builder {
            dialog.button2.setOnClickListener(onClick)
            return this
        }

        fun setCanceledOnTouchOutside(isCancelOnTouchOutside:Boolean):Builder{
            dialog.setCanceledOnTouchOutside(isCancelOnTouchOutside)
            return this
        }

        fun setDismissListener(listener: DialogInterface.OnDismissListener):Builder {
            dialog.setOnDismissListener(listener)
            return this
        }

        fun dismissDialog() {
            dialog.dismiss()
        }
        open fun show(): AddPhotoDialog {
            dialog.show()
            return dialog
        }
    }
}
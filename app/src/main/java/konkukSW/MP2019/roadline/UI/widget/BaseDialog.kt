package konkukSW.MP2019.roadline.UI.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.base_dialog.*

open class BaseDialog(context: Context) : Dialog(context) {

    open class Builder(val mContext: Context) {
        open val dialog = BaseDialog(mContext)
        open fun create(): Builder {
            dialog.create()
            dialog.setContentView(R.layout.base_dialog)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            var size = Point()
            dialog.window!!.windowManager.defaultDisplay.getSize(size)
            dialog.window!!.setLayout((size.x * 0.872f).toInt(), size.y)
            return this
        }
        

        fun setTitle(text: String): Builder {
            dialog.tvDialogTitle.visibility = View.VISIBLE
            dialog.dialogDivider.visibility = View.VISIBLE
            dialog.tvDialogTitle.text = text
            return this
        }

        fun setMessage(text: String): Builder {
            dialog.tvDialogText.visibility = View.VISIBLE
            dialog.tvDialogText.text = text
            return this
        }
        fun setMessage(text: SpannableStringBuilder): Builder {
            dialog.tvDialogText.visibility = View.VISIBLE
            dialog.tvDialogText.text = text
            return this
        }
        fun setGuideMessage(text: String): Builder {
            dialog.tvDialogTextGuide.visibility = View.VISIBLE
            dialog.tvDialogTextGuide.text = text
            return this
        }
        fun setGuideMessage(text: SpannableStringBuilder): Builder {
            dialog.tvDialogTextGuide.visibility = View.VISIBLE
            dialog.tvDialogTextGuide.text = text
            return this
        }

        open fun setCancelButton(text: String, onClick: View.OnClickListener? = null): Builder {
            dialog.btnCancel.text = text
            if(onClick == null)
                dialog.btnCancel.setOnClickListener { dialog.dismiss() }
            else
                dialog.btnCancel.setOnClickListener(onClick)
            dialog.btnCancel.visibility = View.VISIBLE
            dialog.dialogLinearLayout.visibility = View.VISIBLE
            return this
        }

        open fun setOkButton(text: String, onClick: View.OnClickListener): Builder {
            dialog.btnOk.text = text
            dialog.btnOk.setOnClickListener(onClick)
            dialog.btnOk.visibility = View.VISIBLE
            dialog.dialogLinearLayout.visibility = View.VISIBLE
            return this
        }
        fun setDismissListener(listener: DialogInterface.OnDismissListener):Builder {
            dialog.setOnDismissListener(listener)
            return this
        }

        fun dismissDialog() {
            dialog.dismiss()
        }
        open fun show(): BaseDialog {
            dialog.show()
            return dialog
        }
    }
}
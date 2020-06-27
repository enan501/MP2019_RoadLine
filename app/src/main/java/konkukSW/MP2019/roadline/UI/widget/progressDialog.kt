package konkukSW.MP2019.roadline.UI.widget

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.dialog_progress_screen.*

class ProgressDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress_screen)
    }

    class Builder(val mContext: Context) {
        open val dialog = ProgressDialog(mContext)

        fun create(): Builder {
            dialog.create()
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            return this
        }

        fun setColor(color:Int):Builder{
            dialog.progressBar.indeterminateDrawable.setTint(color)
            return this
        }

        fun dismissDialog() {
            dialog.dismiss()
        }

        fun show(): ProgressDialog {
            dialog.show()
            return dialog
        }


    }
}
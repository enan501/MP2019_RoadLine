package konkukSW.MP2019.roadline.UI.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import konkukSW.MP2019.roadline.Data.Adapter.CurrencyAdapter
import konkukSW.MP2019.roadline.R
import kotlinx.android.synthetic.main.add_list_dialog.*

open class AddListDialog(context: Context) : Dialog(context) {

    open class Builder(val mContext: Context) {
        open val dialog = AddListDialog(mContext)
        open fun create(): Builder {
            dialog.create()
            dialog.setContentView(R.layout.add_list_dialog)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            var size = Point()
            dialog.window!!.windowManager.defaultDisplay.getSize(size)
            dialog.window!!.setLayout((size.x * 0.872f).toInt(), size.y)
            return this
        }
        fun setCurrencyAdapter(adapter: ArrayAdapter<String>):Builder {
            dialog.AL_currencySpinner.adapter = adapter
            if (Build.VERSION.SDK_INT >= 26) {
                dialog.AL_currencySpinner.setAutofillHints("화폐를 검색하세요")
            } else {
                dialog.AL_currencySpinner.setTitle("")
            }
            dialog.AL_currencySpinner.setPositiveButton("취소")


            dialog.AL_currencySpinner.setSelection(adapter.count)
            return this
        }
        fun setCurrencySelectedListener(listener: AdapterView.OnItemSelectedListener):Builder {
            dialog.AL_currencySpinner.onItemSelectedListener = listener
            return this
        }
        fun setSelectedCurrencyAdapter(adapter: CurrencyAdapter):Builder{
            dialog.rvCurrency.adapter = adapter
            return this
        }
        fun setDateListener(startListener:View.OnClickListener, endListener:View.OnClickListener):Builder{
            dialog.editStart.setOnClickListener(startListener)
            dialog.editEnd.setOnClickListener(endListener)
            return this
        }

//        fun setTitle(text: String): Builder {
//            //dialog.dialogDivider.visibility = View.VISIBLE
//            dialog.AL_dialogTitle.text = text
//            return this
//        }

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
        fun setCanceledOnTouchOutside(isCancelOnTouchOutside:Boolean):Builder{
            dialog.setCanceledOnTouchOutside(isCancelOnTouchOutside)
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
        open fun show(): AddListDialog {
            dialog.show()
            return dialog
        }


    }
}
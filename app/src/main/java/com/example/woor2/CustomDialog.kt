package com.example.woor2

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class CustomDialog(context: Context) {
        private val dialog = Dialog(context)

    fun myDig() {
        dialog.setContentView(R.layout.custom_dialog)

        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)

        //val edit = dialog.findViewById<EditText>(R.id.editTextName)
        val btnOK = dialog.findViewById<Button>(R.id.buttonOk)
        val btnCancel = dialog.findViewById<Button>(R.id.buttonCancel)

        btnOK.setOnClickListener {
            onClickListener.onClicked()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    interface ButtonClickListener {
        fun onClicked()
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickListener(listener: ButtonClickListener) {
        onClickListener = listener
    }

    fun changeText(text: String) {
        val textView = dialog.findViewById<TextView>(R.id.textView6)
        textView.text = text
    }
}
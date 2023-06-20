package com.dscreate_app.cashbash.utils.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.dscreate_app.cashbash.databinding.ProgressDialogLayoutBinding

object ProgressDialog {

    fun createProgressDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val binding = ProgressDialogLayoutBinding.inflate(
            LayoutInflater.from(context)
        )
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(false) // добавл возможность блокировки экрана без возможности отмены вручную, через false. Отмена через dialog.dismiss() в коде.
        dialog.show()
        return dialog
    }

}
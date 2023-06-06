package com.dscreate_app.cashbash.dialogs

import android.app.AlertDialog
import android.view.LayoutInflater
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.MainActivity
import com.dscreate_app.cashbash.databinding.SignDialogBinding
import com.dscreate_app.cashbash.utils.AccountHelper
import com.dscreate_app.cashbash.utils.showToast

class DialogHelper(private val mainAct: MainActivity) {
    private val accHelper = AccountHelper(mainAct)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(mainAct)
        val binding = SignDialogBinding.inflate(LayoutInflater.from(mainAct))
        builder.setView(binding.root)
        if (index == SIGN_UP_STATE) {
            binding.tvSignTitle.text = mainAct.getString(R.string.ac_sign_up)
            binding.btSignUpIn.text = mainAct.getString(R.string.sign_up_action)
        } else {
            binding.tvSignTitle.text = mainAct.getString(R.string.ac_sign_in)
            binding.btSignUpIn.text = mainAct.getString(R.string.sign_in_action)
        }
        val dialog = builder.create()
        binding.btSignUpIn.setOnClickListener {
            dialog.dismiss()
            if (index == SIGN_UP_STATE) {
                accHelper.signUpWithEmail(
                    binding.edSignEmail.text.toString(), binding.edPassword.text.toString()
                )
            } else {
                accHelper.signInWithEmail(
                    binding.edSignEmail.text.toString(), binding.edPassword.text.toString()
                )
                mainAct.showToast(mainAct.getString(R.string.sign_in_finish))
            }
        }
        dialog.show()
    }

   companion object {
       const val SIGN_UP_STATE = 0
       const val SIGN_IN_STATE = 1
   }
}
package com.dscreate_app.cashbash.dialogs

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.MainActivity
import com.dscreate_app.cashbash.databinding.SignDialogBinding
import com.dscreate_app.cashbash.utils.AccountHelper
import com.dscreate_app.cashbash.utils.showToast

class DialogHelper(private val mainAct: MainActivity) {
    val accHelper = AccountHelper(mainAct)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(mainAct)
        val binding = SignDialogBinding.inflate(LayoutInflater.from(mainAct))
        builder.setView(binding.root)
        setDialogState(index, binding)
        val dialog = builder.create()
        onClicks(binding, index, dialog)
        dialog.show()
    }

    private fun setDialogState(index: Int, binding: SignDialogBinding) {
        if (index == SIGN_UP_STATE) {
            binding.tvSignTitle.text = mainAct.getString(R.string.ac_sign_up)
            binding.btSignUpIn.text = mainAct.getString(R.string.sign_up_action)
        } else {
            binding.tvSignTitle.text = mainAct.getString(R.string.ac_sign_in)
            binding.btSignUpIn.text = mainAct.getString(R.string.sign_in_action)
            binding.btForgetPass.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSignUpIn(index: Int, binding: SignDialogBinding, dialog: AlertDialog?) {
            dialog?.dismiss()
            if (index == SIGN_UP_STATE) {
                accHelper.signUpWithEmail(
                    binding.edSignEmail.text.toString(), binding.edPassword.text.toString()
                )
            } else {
                accHelper.signInWithEmail(
                    binding.edSignEmail.text.toString(), binding.edPassword.text.toString()
                )
        }
    }

    private fun setOnClickResetPassword(binding: SignDialogBinding, dialog: AlertDialog?) {
        if (binding.edSignEmail.text.isNotEmpty()) {
            mainAct.mAuth.sendPasswordResetEmail(binding.edSignEmail.text.toString())
                .addOnCompleteListener {
                    task ->
                    if (task.isSuccessful) {
                        mainAct.showToast(mainAct.getString(R.string.reset_email_password))
                    }
                }
            dialog?.dismiss()
        } else {
            binding.tvDialogMessage.visibility = View.VISIBLE
        }
    }

    private fun onClicks(binding: SignDialogBinding, index: Int, dialog: AlertDialog?) {
        binding.btSignUpIn.setOnClickListener {
            setOnClickSignUpIn(index, binding, dialog)
        }
        binding.btForgetPass.setOnClickListener {
            setOnClickResetPassword(binding, dialog)
        }
        binding.btGoogleSignIn.setOnClickListener {
            accHelper.signInWithGoogle()
            dialog?.dismiss()
        }
    }

   companion object {
       const val SIGN_UP_STATE = 0
       const val SIGN_IN_STATE = 1
   }
}
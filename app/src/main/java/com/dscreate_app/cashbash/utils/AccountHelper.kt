package com.dscreate_app.cashbash.utils

import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.MainActivity
import com.google.firebase.auth.FirebaseUser

class AccountHelper(private val mainAct: MainActivity) {

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotBlank() && password.isNotEmpty()) {
            mainAct.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    task.result.user?.let { sendEmailVerification(it) }
                } else {
                    mainAct.showToast(mainAct.getString(R.string.sign_up_error))
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                mainAct.showToast(mainAct.getString(R.string.send_verification_email))
            } else {
                mainAct.showToast(mainAct.getString(R.string.send_verification_email_error))
            }
        }
    }
}
package com.dscreate_app.cashbash.utils

import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.MainActivity
import com.dscreate_app.cashbash.utils.GoogleAccountConst.GOOGLE_SIGN_IN_REQUEST_CODE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AccountHelper(private val mainAct: MainActivity) {

    private lateinit var googleSignInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotBlank() && password.isNotEmpty()) {
            mainAct.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    task.result.user?.let { sendEmailVerification(it) }
                    mainAct.uiUpdate(task.result.user)
                } else {
                    mainAct.showToast(mainAct.getString(R.string.sign_up_error))
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotBlank() && password.isNotEmpty()) {
            mainAct.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    mainAct.uiUpdate(task.result.user)
                } else {
                    mainAct.showToast(mainAct.getString(R.string.sign_in_error))
                }
            }
        }
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        mainAct.mAuth.signInWithCredential(credential).addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                mainAct.showToast(mainAct.getString(R.string.sign_in_successful))
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

    fun signInWithGoogle() {
        googleSignInClient = getSignInClient()
        val intent = googleSignInClient.signInIntent
        mainAct.startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(mainAct.getString(R.string.default_web_client_id)).build()
        return GoogleSignIn.getClient(mainAct, gso)
    }

    companion object {
    }
}
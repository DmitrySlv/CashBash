package com.dscreate_app.cashbash.utils.firebase

import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.MainActivity
import com.dscreate_app.cashbash.utils.firebase.GoogleAccountConst.GOOGLE_SIGN_IN_REQUEST_CODE
import com.dscreate_app.cashbash.utils.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AccountHelper(private val mainAct: MainActivity) {

    private lateinit var googleSignInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotBlank() && password.isNotEmpty()) {
            mainAct.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.user?.let { sendEmailVerification(it) }
                        mainAct.uiUpdate(task.result.user)
                    } else {
                        //  mainAct.showToast(mainAct.getString(R.string.sign_up_error))
                       // mainAct.logD("MyLog", "Exception: ${task.exception}")
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            val exception = task.exception as FirebaseAuthUserCollisionException
                            //  mainAct.logD("MyLog", "Exception: ${exception.errorCode}")
                            if (exception.errorCode == ERROR_EMAIL_ALREADY_IN_USE) {
                                linkEmailToGoogle(email, password)
                            }
                        }
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException
                            if (exception.errorCode == ERROR_INVALID_EMAIL) {
                                mainAct.showToast(ERROR_INVALID_EMAIL)
                            }
                        }
                        if (task.exception is FirebaseAuthWeakPasswordException) {
                            val exception = task.exception as FirebaseAuthWeakPasswordException
                            if (exception.errorCode == ERROR_WEAK_PASSWORD)
                           mainAct.showToast(ERROR_WEAK_PASSWORD)
                        }
                    }
                }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            mainAct.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    mainAct.uiUpdate(task.result.user)
                } else {
                  //  mainAct.logD(TAG, "Exception 2: ${task.exception} ")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val exception =
                            task.exception as FirebaseAuthInvalidCredentialsException
                      //  mainAct.logD(TAG, "Exception 2: ${exception.errorCode} ")
                        if (exception.errorCode == ERROR_WRONG_PASSWORD) {
                            mainAct.showToast(ERROR_WRONG_PASSWORD)
                        }
                    }
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        val exception = task.exception as FirebaseAuthInvalidUserException
                       if (exception.errorCode == ERROR_USER_NOT_FOUND) {
                           mainAct.showToast(ERROR_USER_NOT_FOUND)
                       }
                    }
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
                mainAct.uiUpdate(task.result?.user)
            } else {
                mainAct.showToast(mainAct.getString(R.string.sign_in_firebase_error))
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

    fun signOutGoogle() {
        getSignInClient().signOut()
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(mainAct.getString(R.string.default_web_client_id))
            .requestEmail().build()
        return GoogleSignIn.getClient(mainAct, gso)
    }

    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (mainAct.mAuth.currentUser != null) {
            mainAct.mAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mainAct.showToast(mainAct.getString(R.string.link_done))
                    }
                }
        } else {
            mainAct.showToast(mainAct.getString(R.string.enter_to_google_acc))
        }
    }

    companion object {
      private const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
      private const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
      private const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"
      private const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"
      private const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
      private const val TAG = "MyLog"
    }
}
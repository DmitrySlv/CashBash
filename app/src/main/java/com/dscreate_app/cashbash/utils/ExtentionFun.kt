package com.dscreate_app.cashbash.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.showToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(string: String) {
    Toast.makeText(activity, string, Toast.LENGTH_LONG).show()
}

fun AppCompatActivity.LogD(tag: String, msg: String) {
   android.util.Log.d(tag, msg)
}

fun Fragment.LogD(tag: String, msg: String) {
    android.util.Log.d(tag, msg)
}
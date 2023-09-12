package com.dscreate_app.cashbash.utils

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.showToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(string: String) {
    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.logD(tag: String, msg: String) {
  Log.d(tag, msg)
}

fun Fragment.logD(tag: String, msg: String) {
    Log.d(tag, msg)
}
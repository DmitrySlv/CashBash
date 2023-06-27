package com.dscreate_app.cashbash.data

import com.dscreate_app.cashbash.data.models.AdModelDto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val db = Firebase.database.getReference(MAIN_PATH)
    private val auth = Firebase.auth

    fun publishAd(adModel: AdModelDto) {
        auth.uid?.let { uid ->
            db.child(adModel.key ?: EMPTY).child(uid).child(AD_PATH).setValue(adModel)
        }
    }

    companion object {
        private const val MAIN_PATH = "main"
        private const val AD_PATH = "ad"
        private const val EMPTY = "empty"
    }
}
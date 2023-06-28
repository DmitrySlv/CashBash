package com.dscreate_app.cashbash.data

import com.dscreate_app.cashbash.data.models.AdModelDto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    fun readDataFromDb() {
        //Данные обновляются с этим listener порционально постранично.
        db.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val adList = mutableListOf<AdModelDto>()
                for (item in snapshot.children) {
                    val ad = item.children
                        .iterator()
                        .next()
                        .child(AD_PATH)
                        .getValue(AdModelDto::class.java)
                    ad?.let {
                        adList.add(ad)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    companion object {
        private const val MAIN_PATH = "main"
        private const val AD_PATH = "ad"
        private const val EMPTY = "empty"
        private const val TAG = "MyLog"
    }
}
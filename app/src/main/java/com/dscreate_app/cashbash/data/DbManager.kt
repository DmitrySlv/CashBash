package com.dscreate_app.cashbash.data

import com.dscreate_app.cashbash.data.models.AdModelDto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val db = Firebase.database.getReference(MAIN_PATH)
    val auth = Firebase.auth

    fun publishAd(adModel: AdModelDto) {
        auth.uid?.let { uid ->
            db.child(adModel.key ?: EMPTY).child(uid).child(AD_PATH).setValue(adModel)
        }
    }

   private fun readDataFromDb(query: Query, readCallback: ReadDataCallback?) {
        //Данные обновляются с этим listener порционально постранично.
        query.addListenerForSingleValueEvent(object: ValueEventListener {

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
                    readCallback?.readData(adList)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getMyAds(readCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readCallback)
    }

    fun getAllAds(readCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + "/ad/price")
        readDataFromDb(query, readCallback)
    }

    interface ReadDataCallback {
        fun readData(list: MutableList<AdModelDto>)
    }

    companion object {
        private const val MAIN_PATH = "main"
        private const val AD_PATH = "ad"
        private const val EMPTY = "empty"
        private const val TAG = "MyLog"
    }
}
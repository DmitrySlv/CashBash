package com.dscreate_app.cashbash.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.data.models.InfoItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DbManager {
    val db = Firebase.database.getReference(MAIN_PATH)
    val dbStorage = Firebase.storage.getReference(MAIN_PATH)
    val auth = Firebase.auth

    fun publishAd(adModel: AdModelDto, finishWorkListener: FinishWorkListener, context: Context) {
        auth.uid?.let { uid ->
            db.child(adModel.key ?: EMPTY).child(uid).child(AD_PATH).setValue(adModel)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        finishWorkListener.onFinish()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_load_ad_to_firebase_toast),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

   private fun readDataFromDb(query: Query, readCallback: ReadDataCallback?) {
        //Данные обновляются с этим listener порционально постранично один раз за считывание с БД.
        query.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val adList = mutableListOf<AdModelDto>()
                for (item in snapshot.children) {

                    var ad: AdModelDto? = null
                    item.children.forEach {
                        if (ad == null) {
                            ad = it.child(AD_PATH).getValue(AdModelDto::class.java)
                        }
                    }
                    val infoItem = item.child(INFO_PATH).getValue(InfoItem::class.java)
                    val favCounter =  item.child(FAVS_PATH).childrenCount
                    val isFav = auth.uid?.let { uid ->
                        item.child(FAVS_PATH).child(uid).getValue(String::class.java)
                    }
                    ad?.isFavourite = isFav != null
                    ad?.favCounter = favCounter.toString()

                    ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                    ad?.emailsCounter = infoItem?.emailsCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"
                    ad?.let {
                        adList.add(ad!!)
                    }
                    readCallback?.readData(adList)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun onFavouriteClick(adModel: AdModelDto, finishWorkListener: FinishWorkListener) {
        if (adModel.isFavourite) {
            removeFromFavourites(adModel, finishWorkListener)
        } else {
            addToFavourites(adModel, finishWorkListener)
        }
    }

   private fun addToFavourites(adModel: AdModelDto, finishWorkListener: FinishWorkListener) {
        adModel.key?.let { key ->
            auth.uid?.let { uid ->
                db.child(key).child(FAVS_PATH).child(uid).setValue(uid).addOnCompleteListener {
                    if (it.isSuccessful) {
                        finishWorkListener.onFinish()
                    }
                }
            }
        }
    }

    private fun removeFromFavourites(adModel: AdModelDto, finishWorkListener: FinishWorkListener) {
        adModel.key?.let { key ->
            auth.uid?.let { uid ->
                db.child(key).child(FAVS_PATH).child(uid).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        finishWorkListener.onFinish()
                    }
                }
            }
        }
    }

    fun getMyAds(readCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + AD_UID_PATH).equalTo(auth.uid)
        readDataFromDb(query, readCallback)
    }

    fun getMyFavourites(readCallback: ReadDataCallback?) {
        val query = db.orderByChild( "/favourites/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(query, readCallback)
    }

    fun getAllAds(readCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + AD_PRICE_PATH)
        readDataFromDb(query, readCallback)
    }

    fun deleteAd(adModel: AdModelDto, context: Context, listener: FinishWorkListener) {
        if (adModel.key == null || adModel.uid == null) return
        db.child(adModel.key).child(adModel.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onFinish()
            } else {
                Toast.makeText(context,
                    context.getString(R.string.error_delete_from_firebase),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun adViewed(adModel: AdModelDto) {
        var counter = adModel.viewsCounter.toInt()
        counter++
        auth.uid?.let {
            db.child(adModel.key ?: EMPTY).child(INFO_PATH).setValue(
                InfoItem(counter.toString(), adModel.emailsCounter, adModel.callsCounter)
            )
        }
    }

    interface ReadDataCallback {
        fun readData(list: MutableList<AdModelDto>)
    }

    interface FinishWorkListener {
        fun onFinish()
    }

    companion object {
        private const val MAIN_PATH = "main"
        private const val AD_PATH = "ad"
        private const val INFO_PATH = "info"
        private const val FAVS_PATH = "favourites"
        private const val AD_UID_PATH = "/ad/uid"
        private const val AD_PRICE_PATH = "/ad/price"
        private const val EMPTY = "empty"
        private const val TAG = "MyLog"
    }
}
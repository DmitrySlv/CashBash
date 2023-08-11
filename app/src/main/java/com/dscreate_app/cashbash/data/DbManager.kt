package com.dscreate_app.cashbash.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.data.models.InfoItem
import com.dscreate_app.cashbash.utils.FilterManager
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
                    val adFilter = FilterManager.createFilter(adModel)
                    db.child(adModel.key ?: EMPTY).child(AD_FILTER_PATH)
                        .setValue(adFilter).addOnCompleteListener {
                            if (it.isSuccessful) {
                                finishWorkListener.onFinish(it.isSuccessful)
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
                }
                readCallback?.readData(adList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun readNextPageFromDb(
        query: Query, filter: String, orderBy: String, readCallback: ReadDataCallback?
    ) {
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
                    val filterNodeValue = item.child(AD_FILTER_PATH).child(orderBy).value.toString()
                    Log.d(TAG, "Filter value: $filterNodeValue")
                    val favCounter =  item.child(FAVS_PATH).childrenCount
                    val isFav = auth.uid?.let { uid ->
                        item.child(FAVS_PATH).child(uid).getValue(String::class.java)
                    }
                    ad?.isFavourite = isFav != null
                    ad?.favCounter = favCounter.toString()

                    ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                    ad?.emailsCounter = infoItem?.emailsCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"
                    if (ad != null && filterNodeValue.startsWith(filter)) adList.add(ad!!)
                }
                readCallback?.readData(adList)
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
                        finishWorkListener.onFinish(true)
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
                        finishWorkListener.onFinish(true)
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

    fun getAllAdsFirstPage(filter: String, readCallback: ReadDataCallback?) {
        val query = if (filter.isEmpty()) {
            db.orderByChild(AD_FILTER_TIME_PATH).limitToLast(ADS_LIMIT)
        } else {
            getAllAdsByFilterFirstPage(filter)
        }
        readDataFromDb(query, readCallback)
    }

   private fun getAllAdsByFilterFirstPage(tempFilter: String): Query {
       val orderBy = tempFilter.split("|")[0]
       val filter = tempFilter.split("|")[1]
        return db.orderByChild("/adFilter/${orderBy}")
            .startAt(filter).endAt(filter + SYM_FROM_TIME_WITHOUT).limitToLast(ADS_LIMIT)
    }

    fun getAllAdsNextPage(time: String, filter: String, readCallback: ReadDataCallback?) {
       if (filter.isEmpty()) {
          val query =  db.orderByChild(AD_FILTER_TIME_PATH)
              .endBefore(time).limitToLast(ADS_LIMIT)
           readDataFromDb(query, readCallback)
        } else {
            getAllAdsByFilterNextPage(filter, time,readCallback)
        }
    }


    private fun getAllAdsByFilterNextPage(
        tempFilter: String, time: String, readCallback: ReadDataCallback?
    ) {
        val orderBy = tempFilter.split("|")[0]
        val filter = tempFilter.split("|")[1]
        val query = db.orderByChild("/adFilter/${orderBy}")
            .endBefore(filter + "_$time").limitToLast(ADS_LIMIT)
        readNextPageFromDb(query, filter, orderBy, readCallback)
    }

    fun getAllAdsFromCatFirstPage(cat: String, filter: String, readCallback: ReadDataCallback?) {
        val query = if (filter.isEmpty()) {
            db.orderByChild(AD_FILTER_CAT_TIME_PATH)
                .startAt(cat).endAt(cat + SPECIAL_SYM_FROM_TIME).limitToLast(ADS_LIMIT)
        } else {
            getAllAdsFromCatByFilterFirstPage(cat, filter)
        }
        readDataFromDb(query, readCallback)
    }

    private fun getAllAdsFromCatByFilterFirstPage(cat: String, tempFilter: String): Query {
        val orderBy = CAT_ + tempFilter.split("|")[0]
        val filter = cat + DELIMITER_ + tempFilter.split("|")[1]
        return db.orderByChild("/adFilter/$orderBy")
            .startAt(filter).endAt(filter + SYM_FROM_TIME_WITHOUT).limitToLast(ADS_LIMIT)
    }

    fun getAllAdsFromCatNextPage(cat: String, time: String, filter: String, readCallback: ReadDataCallback?) {
        if (filter.isEmpty()) {
            val query = db.orderByChild(AD_FILTER_CAT_TIME_PATH)
                .endBefore(cat + DELIMITER_ + time).limitToLast(ADS_LIMIT)
            readDataFromDb(query, readCallback)
        } else {
            getAllAdsFromCatByFilterNextPage(cat, time, filter, readCallback)
        }
    }

    private fun getAllAdsFromCatByFilterNextPage(
        cat: String, time: String, tempFilter: String, readCallback: ReadDataCallback?
    ) {
        val orderBy = CAT_ + tempFilter.split("|")[0]
        val filter = cat + DELIMITER_ + tempFilter.split("|")[1]
        val query = db.orderByChild("/adFilter/$orderBy")
            .endBefore(filter + DELIMITER_ + time).limitToLast(ADS_LIMIT)
        readNextPageFromDb(query, filter, orderBy, readCallback)
    }

    fun deleteAd(adModel: AdModelDto, context: Context, listener: FinishWorkListener) {
        if (adModel.key == null || adModel.uid == null) return
        db.child(adModel.key).child(adModel.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onFinish(true)
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
        fun onFinish(isDone: Boolean)
    }

    companion object {
        private const val MAIN_PATH = "main"
        private const val AD_PATH = "ad"
        private const val INFO_PATH = "info"
        private const val FAVS_PATH = "favourites"
        private const val AD_FILTER_PATH = "adFilter"
        private const val AD_UID_PATH = "/ad/uid"
        private const val AD_TIME_PATH = "/ad/time"
        private const val SPECIAL_SYM_FROM_TIME = "_\uf8ff" // \uf8ff - подставляет автоматич нужное время
        private const val SYM_FROM_TIME_WITHOUT = "\uf8ff"
        private const val AD_FILTER_TIME_PATH = "/adFilter/time"
        private const val AD_FILTER_CAT_TIME_PATH = "/adFilter/cat_time"
        private const val ADS_LIMIT = 2
        private const val EMPTY = "empty"
        private const val CAT_ = "cat_"
        private const val DELIMITER_ = "_"
        private const val TAG = "MyLog"
    }
}
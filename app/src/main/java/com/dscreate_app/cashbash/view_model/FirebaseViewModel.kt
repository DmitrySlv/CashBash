package com.dscreate_app.cashbash.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dscreate_app.cashbash.data.DbManager
import com.dscreate_app.cashbash.data.models.AdModelDto

class FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()

    private var _liveAdsData = MutableLiveData<MutableList<AdModelDto>>()
    val liveAdsData: LiveData<MutableList<AdModelDto>>
        get() = _liveAdsData

    fun loadAllAds() {
        dbManager.getAllAds(object : DbManager.ReadDataCallback {

            override fun readData(list: MutableList<AdModelDto>) {
                _liveAdsData.value = list
            }
        })
    }

    fun loadMyAds() {
        dbManager.getMyAds(object : DbManager.ReadDataCallback {

            override fun readData(list: MutableList<AdModelDto>) {
                _liveAdsData.value = list
            }
        })
    }

    fun deleteItem(adModel: AdModelDto, context: Context) {
        dbManager.deleteAd(adModel, context, object:  DbManager.FinishWorkListener {
            override fun onFinnish() {
                val updatedList = _liveAdsData.value
                updatedList?.remove(adModel)
                _liveAdsData.postValue(updatedList)
            }
        })
    }
}
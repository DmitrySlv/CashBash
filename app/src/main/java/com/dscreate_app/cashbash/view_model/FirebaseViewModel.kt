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

    fun loadAllAdsFirstPage() {
        dbManager.getAllAdsFirstPage(object : DbManager.ReadDataCallback {
            override fun readData(list: MutableList<AdModelDto>) {
                _liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsNextPage(time: String) {
        dbManager.getAllAdsNextPage(time, object : DbManager.ReadDataCallback {
            override fun readData(list: MutableList<AdModelDto>) {
                _liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsFromCat(cat: String) {
        dbManager.getAllAdsFromCatFirstPage(cat, object : DbManager.ReadDataCallback {
            override fun readData(list: MutableList<AdModelDto>) {
                _liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsFromCatNextPage(catTime: String) {
        dbManager.getAllAdsFromCatNextPage(catTime, object : DbManager.ReadDataCallback {
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

    fun loadMyFavourites() {
        dbManager.getMyFavourites(object : DbManager.ReadDataCallback {
            override fun readData(list: MutableList<AdModelDto>) {
                _liveAdsData.value = list
            }
        })
    }

    fun deleteItem(adModel: AdModelDto, context: Context) {
        dbManager.deleteAd(adModel, context, object:  DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = _liveAdsData.value
                updatedList?.remove(adModel)
                _liveAdsData.postValue(updatedList)
            }
        })
    }

    fun adViewed(adModel: AdModelDto) {
        dbManager.adViewed(adModel)
    }

    fun onFavouriteClick(adModel: AdModelDto) {
        dbManager.onFavouriteClick(adModel, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = _liveAdsData.value
                val position = updatedList?.indexOf(adModel)
                if (position != -1) {
                    position?.let {
                        val favCounter = if (adModel.isFavourite) {
                            adModel.favCounter.toInt() - 1
                        } else {
                            adModel.favCounter.toInt() + 1
                        }
                        updatedList[position] = updatedList[position].copy(
                            isFavourite = !adModel.isFavourite,
                            favCounter = favCounter.toString()
                        )
                    }
                }
                _liveAdsData.postValue(updatedList)
            }
        })
    }
}
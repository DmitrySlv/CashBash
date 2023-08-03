package com.dscreate_app.cashbash.utils

import com.dscreate_app.cashbash.data.models.AdFilterDto
import com.dscreate_app.cashbash.data.models.AdModelDto

object FilterManager {

    fun createFilter(ad: AdModelDto): AdFilterDto {
        return AdFilterDto(
            ad.time,
            "${ad.category}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.country}_${ad.city}_${ad.index}" +
                    "_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.withSend}_${ad.time}",

            "${ad.country}_${ad.withSend}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.withSend}_${ad.time}",
            "${ad.country}_${ad.city}_${ad.index}" +
                    "_${ad.withSend}_${ad.time}",
            "${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.withSend}_${ad.time}"
        )
    }

    fun getFilter(filter: String): String {
        val sBuilder = StringBuilder()
        val tempList = filter.split("_")
        if (tempList[0] != EMPTY) sBuilder.append(COUNTRY_)
        if (tempList[1] != EMPTY) sBuilder.append(CITY_)
        if (tempList[2] != EMPTY) sBuilder.append(INDEX_)
        sBuilder.append(WITH_SEND_TIME)
        return sBuilder.toString()
    }

    private const val EMPTY = "empty"
    private const val COUNTRY_ = "country_"
    private const val CITY_ = "city_"
    private const val INDEX_ = "index_"
    private const val WITH_SEND_TIME = "withSend_time"
}
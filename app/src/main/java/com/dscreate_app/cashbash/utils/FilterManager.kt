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
        val sBuilderPath = StringBuilder()
        val sBuilderFilter = StringBuilder()
        val tempList = filter.split("_")
        if (tempList[0] != EMPTY) {
            sBuilderPath.append(COUNTRY_)
            sBuilderFilter.append("${tempList[0]}_")
        }
        if (tempList[1] != EMPTY) {
            sBuilderPath.append(CITY_)
            sBuilderFilter.append("${tempList[1]}_")
        }
        if (tempList[2] != EMPTY){
            sBuilderPath.append(INDEX_)
            sBuilderFilter.append("${tempList[2]}_")
        }
        sBuilderFilter.append(tempList[3])
        sBuilderPath.append(WITH_SEND_TIME)

        return "$sBuilderPath|$sBuilderFilter"
    }

    private const val EMPTY = "empty"
    private const val COUNTRY_ = "country_"
    private const val CITY_ = "city_"
    private const val INDEX_ = "index_"
    private const val WITH_SEND_TIME = "withSend_time"
}
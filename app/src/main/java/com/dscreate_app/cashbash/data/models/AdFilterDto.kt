package com.dscreate_app.cashbash.data.models

data class AdFilterDto(
    val time: String? = null,
    val cat_time: String? = null,

    //Filter with category
    val cat_country_withSend_time: String? = null,
    val cat_country_city_withSend_time: String? = null,
    val cat_country_city_index_withSend_time: String? = null,
    val cat_index_withSend_time: String? = null,
    val cat_withSend_time: String? = null,

    //Filter without category
    val country_withSend_time: String? = null,
    val country_city_withSend_time: String? = null,
    val country_city_index_withSend_time: String? = null,
    val index_withSend_time: String? = null,
    val withSend_time: String? = null
)

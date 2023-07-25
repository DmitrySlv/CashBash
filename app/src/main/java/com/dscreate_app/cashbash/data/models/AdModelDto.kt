package com.dscreate_app.cashbash.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdModelDto(
    val key: String? = null,
    val uid: String? = null,
    val country: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val index: String? = null,
    val withSend: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    val mainImage: String? = null,
    val image2: String? = null,
    val image3: String? = null,
    var favCounter: String = "0",
    var isFavourite: Boolean = false,

    // Нужны для передачи данных из класса в класс. Не для записи
    var viewsCounter: String = "0",
    var emailsCounter: String = "0",
    var callsCounter: String = "0"
): Parcelable

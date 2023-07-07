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
    val description: String? = null
) :Parcelable

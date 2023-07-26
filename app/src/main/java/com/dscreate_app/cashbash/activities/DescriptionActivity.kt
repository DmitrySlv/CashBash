package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.ImageAdapter
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.ActivityDeascriptionBinding
import com.dscreate_app.cashbash.utils.image_picker.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescriptionActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDeascriptionBinding.inflate(layoutInflater) }
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        getIntentFromMainAct()
    }

    private fun init() = with(binding) {
        imageAdapter = ImageAdapter()
        vp.adapter = imageAdapter
    }

    private fun fillTextViews(adModel: AdModelDto) = with(binding) {
        adModel.apply {
            tvTitle.text = title
            tvDescription.text = description
            tvPrice.text = price
            tvPhone.text = phone
            tvCountry.text = country
            tvCity.text = city
            tvIndex.text = index
            tvWithSend.text = isWithSend(withSend.toBoolean())
        }
    }

    private fun isWithSend(witSend: Boolean): String {
        return if (witSend) {
            getString(R.string.yes)
        } else {
            getString(R.string.no)
        }
    }

    private fun updateUi(adModel: AdModelDto) {
        fillImageArray(adModel)
        fillTextViews(adModel)
    }

    private fun getIntentFromMainAct() {
        val adModel = intent.parcelable<AdModelDto>(MainActivity.AD_MODEL_DATA)
        if (adModel != null) {
            updateUi(adModel)
        }
    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private fun fillImageArray(adModel: AdModelDto) {
        val listUris = listOf(adModel.mainImage, adModel.image2, adModel.image3)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.getBitmapFromUris(listUris as MutableList<String?>)
            imageAdapter.updateAdapter(bitmapList as MutableList<Bitmap>)
        }
    }
}
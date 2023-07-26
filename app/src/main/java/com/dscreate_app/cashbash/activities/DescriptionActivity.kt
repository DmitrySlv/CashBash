package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
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

    private fun getIntentFromMainAct() {
        val adModel = intent.parcelable<AdModelDto>(MainActivity.AD_MODEL_DATA)
        adModel?.let { fillImageArray(it) }
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
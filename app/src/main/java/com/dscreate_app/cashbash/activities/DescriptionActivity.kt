package com.dscreate_app.cashbash.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.ImageAdapter
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.ActivityDeascriptionBinding
import com.dscreate_app.cashbash.utils.image_picker.ImageManager
import com.dscreate_app.cashbash.utils.showToast
import io.ak1.pix.helpers.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescriptionActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDeascriptionBinding.inflate(layoutInflater) }
    private lateinit var imageAdapter: ImageAdapter
    private var adModel: AdModelDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        getIntentFromMainAct()
        setOnClicksToFButtons()
    }

    private fun init() = with(binding) {
        imageAdapter = ImageAdapter()
        vp.adapter = imageAdapter
        imageChangeCounter()
    }

    private fun setOnClicksToFButtons() = with(binding) {
        fbPhone.setOnClickListener { call() }
        fbEmail.setOnClickListener { sendEmail() }
    }

    private fun fillTextViews(adModel: AdModelDto) = with(binding) {
        adModel.apply {
            tvTitle.text = title
            tvDescription.text = description
            tvEmail.text = email
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
       ImageManager.fillImageArray(adModel, imageAdapter)
        fillTextViews(adModel)
    }

    private fun getIntentFromMainAct() {
        adModel = intent.parcelable(MainActivity.AD_MODEL_DATA)
        adModel?.let { updateUi(it) }
    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private fun call() {
        val callUri = "tel:${adModel?.phone}" //Писать нужно именно "tel:" иначе будет ошибка при нажатии на кнопку звонка
        val intentCall = Intent(Intent.ACTION_DIAL)
        intentCall.data = callUri.toUri()
        startActivity(intentCall)
    }

    private fun sendEmail() {
        val intentSendEmail = Intent(Intent.ACTION_SEND)
        intentSendEmail.type = "message/rfc822" //данный стринг нужно запомнить для отправки этой функции
        intentSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(adModel?.email)) //Нужно чтобы был именно arrayOf. Для вставки в стр email в письме
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.intent_ad_subject))
            putExtra(Intent.EXTRA_TEXT, "Здравствуйте, меня заинтересовало ваше объявление!")
        }
        try {
            startActivity(Intent.createChooser(intentSendEmail, "Открыть с помощью:"))
        } catch (e: ActivityNotFoundException) {
            showToast(getString(R.string.toast_not_found_email_send_app))
        }
    }

    private fun imageChangeCounter() = with(binding) {
        vp.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1}/${vp.adapter?.itemCount}"
                tvImageCounter.text = imageCounter
            }
        })
    }
}
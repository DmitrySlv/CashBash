package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.ImageAdapter
import com.dscreate_app.cashbash.data.DbManager
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.fragments.ImageListFragment
import com.dscreate_app.cashbash.utils.dialogs.CityHelper
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper
import com.dscreate_app.cashbash.utils.image_picker.ImageConst
import com.dscreate_app.cashbash.utils.image_picker.ImageManager
import com.dscreate_app.cashbash.utils.image_picker.PixImagePicker
import com.dscreate_app.cashbash.utils.showToast
import com.google.android.gms.tasks.OnCompleteListener
import java.io.ByteArrayOutputStream

class EditAdsActivity : AppCompatActivity(), ImageListFragment.FragmentClose {

    val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    var imageListFrag: ImageListFragment? = null
    private val dbManager = DbManager()
    var editImagePos = 0
    private var imageIndex = 0
    private var isEditState = false
    private var ad: AdModelDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        onClickSelectCountry()
        onClickSelectCity()
        onClickSelectCat()
        openPixImagePicker()
        onClickPublish()
        checkEditState()
        imageChangeCounter()
    }

    private fun init() = with(binding) {
        imageAdapter = ImageAdapter()
        vpImages.adapter = imageAdapter
    }

    private fun onClickSelectCountry() = with(binding) {
        tvSelectCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@EditAdsActivity)
            dialog.showSpinnerDialog(this@EditAdsActivity, listCountry, tvSelectCountry)
            if (tvSelectCity.text.toString() != getString(R.string.select_city)) {
                tvSelectCity.text = getString(R.string.select_city)
            }
        }
    }

    private fun onClickSelectCity() = with(binding) {
        tvSelectCity.setOnClickListener {
            val selectedCountry = tvSelectCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCity = CityHelper.getAllCities(
                    this@EditAdsActivity, selectedCountry
                )
                dialog.showSpinnerDialog(this@EditAdsActivity, listCity, tvSelectCity)
            } else {
                showToast(getString(R.string.no_select_country))
            }
        }
    }

    private fun onClickSelectCat() = with(binding) {
        tvSelectCat.setOnClickListener {
            val listOfCat = resources.getStringArray(R.array.category).toMutableList() as ArrayList
            dialog.showSpinnerDialog(this@EditAdsActivity, listOfCat, tvSelectCat)
        }
    }

    private fun onClickPublish() {
            binding.btPublish.setOnClickListener {
                if (isFieldsEmpty()) {
                    showToast(getString(R.string.check_fields_before_publish))
                    return@setOnClickListener
                }
                binding.progressLayout.visibility = View.VISIBLE
                ad = fillAdForFirebase()
                uploadImages()
            }
    }

    private fun onPublishFinnish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinish(isDone: Boolean) {
                binding.progressLayout.visibility = View.GONE
                if (isDone) { finish() }
            }
        }
    }

    private fun isFieldsEmpty(): Boolean = with(binding) {
        return tvSelectCountry.text.toString() == getString(R.string.select_country) ||
                tvSelectCity.text.toString() == getString(R.string.select_city) ||
                tvSelectCat.text.toString() == getString(R.string.select_category) ||
                edPhone.text.isEmpty() || edIndex.text.isEmpty() || edDescription.text.isEmpty() ||
                edTitle.text.isEmpty() || edPrice.text.isEmpty()
    }

    private fun fillViews(adModelDto: AdModelDto) = with(binding) {
        adModelDto.apply {
            tvSelectCountry.text = country
            tvSelectCity.text = city
            edPhone.setText(phone)
            edIndex.setText(index)
            cbWithSend.isChecked = withSend.toBoolean()
            tvSelectCat.text = category
            edTitle.setText(title)
            edPrice.setText(price)
            edDescription.setText(description)
            ImageManager.fillImageArray(adModelDto, imageAdapter)
            updateImageCounter(COUNTER)
        }
    }

    private fun editState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun checkEditState() {
        isEditState = editState()
        if (isEditState) {
            ad =  intent.parcelable(MainActivity.AD_MODEL_DATA)
            ad?.let { fillViews(it) }
        }
    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private fun fillAdForFirebase(): AdModelDto {
        val adTemp: AdModelDto
        binding.apply {
            adTemp = AdModelDto(
               ad?.key ?: dbManager.db.push().key,
                dbManager.auth.uid,
                ad?.time ?: System.currentTimeMillis().toString(),
                tvSelectCountry.text.toString(),
                tvSelectCity.text.toString(),
                edPhone.text.toString(),
                edIndex.text.toString(),
                cbWithSend.isChecked.toString(),
                tvSelectCat.text.toString(),
                edTitle.text.toString(),
                edPrice.text.toString(),
                edDescription.text.toString(),
                edEmail.text.toString(),
               ad?.mainImage ?: EMPTY,
                ad?.image2 ?: EMPTY,
                ad?.image3 ?: EMPTY,
                "0"
            )
        }
        return adTemp
    }

    private fun openPixImagePicker() {
        binding.ibEditImage.setOnClickListener {
            if (imageAdapter.imageList.size == 0) {
                PixImagePicker.getMultiImages(this@EditAdsActivity, ImageConst.MAX_COUNT_IMAGES)
            } else {
                openListImageFrag(null)
                imageListFrag?.updateAdapterFromEdit(imageAdapter.imageList)
            }
        }
    }

    fun openListImageFrag(newList: MutableList<Uri>?) {
        imageListFrag = ImageListFragment.newInstance(this)
        newList?.let {
            imageListFrag?.resizeSelectedImages(it,true, this)
        }
        binding.svMain.visibility = View.GONE
        imageListFrag?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, it)
                .commit()
        }
    }

    private fun uploadImages() {
        if (imageIndex == 3) {
            ad?.let { dbManager.publishAd(it, onPublishFinnish(), this) }
            return
        }
        val oldUrl = getUrlFromAd()
        if (imageAdapter.imageList.size > imageIndex) {
            val byteArray = prepareImageByteArray(imageAdapter.imageList[imageIndex])
            if (oldUrl.startsWith(HTTP)) {
                updateImage(byteArray, oldUrl) {
                    nextImage(it.result.toString())
                }
            } else {
                uploadImage(byteArray) {
                    nextImage(it.result.toString())
                }
            }

        } else {
            if (oldUrl.startsWith(HTTP)) {
                deleteImageByUrl(oldUrl) {
                    nextImage(EMPTY)
                }
            } else {
                nextImage(EMPTY)
            }
        }
    }

    private fun nextImage(uri: String) {
        setImageUriToAd(uri)
        imageIndex++
        uploadImages()
    }

    private fun deleteImageByUrl(oldUrl: String, listener: OnCompleteListener<Void>) {
        dbManager.dbStorage.storage.getReferenceFromUrl(oldUrl).delete()
            .addOnCompleteListener(listener)
    }

    private fun setImageUriToAd(uri: String) {
        when(imageIndex) {
            0 -> ad = ad?.copy(mainImage = uri)
            1 -> ad = ad?.copy(image2 = uri)
            2 -> ad = ad?.copy(image3 = uri)
        }
    }

    private fun prepareImageByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
        return outputStream.toByteArray()
    }

    private fun getUrlFromAd(): String {
        return listOf(ad?.mainImage!!, ad?.image2!!, ad?.image3!!)[imageIndex]
    }

    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>) {
        val imStorageRef = dbManager.auth.uid?.let {
            dbManager.dbStorage
                .child(it)
                .child("image_${System.currentTimeMillis()}")
        }
        val uploadTask = imStorageRef?.putBytes(byteArray)
        uploadTask?.continueWithTask { task ->
            imStorageRef.downloadUrl
        }?.addOnCompleteListener(listener)
    }

    private fun updateImage(byteArray: ByteArray,url: String, listener: OnCompleteListener<Uri>) {
        val imStorageRef = dbManager.dbStorage.storage.getReferenceFromUrl(url)
        val uploadTask = imStorageRef.putBytes(byteArray)
        uploadTask.continueWithTask { task ->
            imStorageRef.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun imageChangeCounter() {
        binding.vpImages.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateImageCounter(position)
            }
        })
    }

    private fun updateImageCounter(counter: Int) = with(binding) {
        var index = 1
        val itemCount = vpImages.adapter?.itemCount
        if (itemCount == 0) index = 0
        val imageCounter = "${counter + index}/${itemCount}"
        tvImageCounter.text = imageCounter
    }

    override fun onClose(list: MutableList<Bitmap>) {
        binding.svMain.visibility = View.VISIBLE
        imageAdapter.updateAdapter(list)
        imageListFrag = null
        updateImageCounter(binding.vpImages.currentItem)
    }

    companion object {
        private const val TAG = "MyLog"
        private const val QUALITY = 20
        private const val EMPTY = "empty"
        private const val COUNTER = 0
        private const val HTTP = "http"
    }
}
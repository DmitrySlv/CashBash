package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.ImageAdapter
import com.dscreate_app.cashbash.data.DbManager
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.fragments.ImageListFragment
import com.dscreate_app.cashbash.utils.dialogs.CityHelper
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper
import com.dscreate_app.cashbash.utils.image_picker.ImageConst
import com.dscreate_app.cashbash.utils.image_picker.PixImagePicker
import com.dscreate_app.cashbash.utils.showToast
import com.fxn.utility.PermUtil

class EditAdsActivity : AppCompatActivity(), ImageListFragment.FragmentClose {

    val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    var imageListFrag: ImageListFragment? = null
    private val dbManager = DbManager()
    var launcherMultiSelectImages: ActivityResultLauncher<Intent>? = null
    var launcherSingleSelectImage: ActivityResultLauncher<Intent>? = null
    var editImagePos = 0
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    PixImagePicker.getOptions(
//                        this,
//                        ImageConst.MAX_COUNT_IMAGES,
//                        ImageConst.REQUEST_CODE_GET_IMAGES)
                } else {
                    showToast(getString(R.string.approve_permission_for_your_photo))
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun init() = with(binding) {
        imageAdapter = ImageAdapter()
        vpImages.adapter = imageAdapter
        launcherMultiSelectImages = PixImagePicker.getLauncherForMultiSelectImages(
            this@EditAdsActivity)
        launcherSingleSelectImage = PixImagePicker.getLauncherForSingleImage(
            this@EditAdsActivity)
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
            val adTemp = fillAdForFirebase()
            if (isEditState) {
                dbManager.publishAd(adTemp.copy(key = ad?.key), onPublishFinnish(), this)
            } else {
                dbManager.publishAd(adTemp, onPublishFinnish(), this)
            }
        }
    }

    private fun onPublishFinnish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinnish() {
                finish()
            }
        }
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
        val ad: AdModelDto
        binding.apply {
            ad = AdModelDto(
                dbManager.db.push().key,
                dbManager.auth.uid,
                tvSelectCountry.text.toString(),
                tvSelectCity.text.toString(),
                edPhone.text.toString(),
                edIndex.text.toString(),
                cbWithSend.isChecked.toString(),
                tvSelectCat.text.toString(),
                edTitle.text.toString(),
                edPrice.text.toString(),
                edDescription.text.toString()
            )
        }
        return ad
    }

    private fun openPixImagePicker() {
        binding.ibEditImage.setOnClickListener {
            if (imageAdapter.imageList.size == 0) {
                PixImagePicker.launcher(
                    this@EditAdsActivity,
                    launcherMultiSelectImages,
                    ImageConst.MAX_COUNT_IMAGES
                )
            } else {
                openListImageFrag(null)
                imageListFrag?.updateAdapterFromEdit(imageAdapter.imageList)
            }
        }
    }

    fun openListImageFrag(newList: MutableList<String>?) {
        imageListFrag = ImageListFragment.newInstance(this, newList)
        binding.svMain.visibility = View.GONE
        imageListFrag?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, it)
                .commit()
        }
    }

    override fun onClose(list: MutableList<Bitmap>) {
        binding.svMain.visibility = View.VISIBLE
        imageAdapter.updateAdapter(list)
        imageListFrag = null
    }

    companion object {
        private const val TAG = "MyLog"
    }
}
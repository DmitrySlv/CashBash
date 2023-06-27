package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.ImageAdapter
import com.dscreate_app.cashbash.data.DbManager
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.fragments.ImageListFragment
import com.dscreate_app.cashbash.utils.dialogs.CityHelper
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper
import com.dscreate_app.cashbash.utils.image_picker.ImageManager
import com.dscreate_app.cashbash.utils.image_picker.PixImagePicker
import com.dscreate_app.cashbash.utils.image_picker.ImageConst
import com.dscreate_app.cashbash.utils.logD
import com.dscreate_app.cashbash.utils.showToast
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil

class EditAdsActivity : AppCompatActivity(), ImageListFragment.FragmentClose {

    val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    var imageListFrag: ImageListFragment? = null
    var editImagePos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        onClickSelectCountry()
        onClickSelectCity()
        onClickSelectCat()
        openPixImagePicker()
        onClickPublish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PixImagePicker.showSelectedImages(resultCode, requestCode, data, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PixImagePicker.getImages(
                        this,
                        ImageConst.MAX_COUNT_IMAGES,
                        ImageConst.REQUEST_CODE_GET_IMAGES)
                } else {
                    showToast(getString(R.string.approve_permission_for_your_photo))
                }
                return
            }
        }
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
            val dbManager = DbManager()
            dbManager.publishAd()
        }
    }

    private fun openPixImagePicker() {
        binding.ibEditImage.setOnClickListener {
            if (imageAdapter.imageList.size == 0) {
                PixImagePicker.getImages(
                    this,
                    ImageConst.MAX_COUNT_IMAGES,
                    ImageConst.REQUEST_CODE_GET_IMAGES
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
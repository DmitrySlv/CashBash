package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.ImageAdapter
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

    private val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()
    private lateinit var imageAdapter: ImageAdapter
    private var imageListFrag: ImageListFragment? = null
    var editImagePos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        onClickSelectCountry()
        onClickSelectCity()
        openPixImagePicker()
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
                showToast(getString(R.string.no_selected_country))
            }
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
                openListImageFrag(imageAdapter.imageList)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImageConst.REQUEST_CODE_GET_IMAGES) {

            data?.let {
                val returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                if (returnValue?.size!! > 1 && imageListFrag == null) {
                    openListImageFrag(returnValue)
                }
                if (returnValue.size == 1 && imageListFrag == null) {

                  //  imageAdapter.updateAdapter(returnValue)
                    val tempList = ImageManager.getImageSize(returnValue[0])
                    logD(TAG, "Картинка ширина: ${tempList[0]} высота: ${tempList[1]}")
                }
                if (imageListFrag != null) {

                    imageListFrag?.updateAdapter(returnValue)
                }
            }
        }
        if (resultCode == RESULT_OK && requestCode == ImageConst.REQUEST_CODE_GET_SINGLE_IMAGE) {

            data?.let {
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                uris?.let {
                    imageListFrag?.setSingeImage(uris[0], editImagePos)
                }
            }

        }
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

    private fun openListImageFrag(newList: MutableList<String>) {
        imageListFrag = ImageListFragment.newInstance(this, newList)
        binding.svMain.visibility = View.GONE
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, imageListFrag!!)
            .commit()
    }

    override fun onClose(list: MutableList<String>) {
        binding.svMain.visibility = View.VISIBLE
        imageAdapter.updateAdapter(list)
        imageListFrag = null
    }

    companion object {
        private const val TAG = "MyLog"
    }
}
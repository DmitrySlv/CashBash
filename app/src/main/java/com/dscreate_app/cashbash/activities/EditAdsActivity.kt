package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.fragments.ImageListFragment
import com.dscreate_app.cashbash.utils.CityHelper
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper
import com.dscreate_app.cashbash.utils.image_picker.ImagePicker
import com.dscreate_app.cashbash.utils.image_picker.ImagePickerConst
import com.dscreate_app.cashbash.utils.logD
import com.dscreate_app.cashbash.utils.showToast
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil

class EditAdsActivity : AppCompatActivity(), ImageListFragment.FragmentClose {

    private val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        onClickSelectCountry()
        onClickSelectCity()
        openPixImagePicker()
    }

    private fun init() = with(binding) {
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
            getImages()
        }
    }

    private fun getImages() {
        binding.svMain.visibility = View.GONE
        supportFragmentManager.beginTransaction()
        .replace(R.id.container, ImageListFragment.newInstance(this))
        .commit()
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
                    ImagePicker.getImages(this, ImagePickerConst.COUNT_IMAGES)
                } else {
                   showToast(getString(R.string.approve_permission_for_your_photo))
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImagePickerConst.REQUEST_CODE_GET_IMAGES) {
            data?.let {
                val returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                logD(TAG, "Image :${returnValue?.get(0)}")
                logD(TAG, "Image :${returnValue?.get(1)}")
                logD(TAG, "Image :${returnValue?.get(2)}")
            }
        }
    }

    override fun onClose() {
        binding.svMain.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "MyLog"
    }
}
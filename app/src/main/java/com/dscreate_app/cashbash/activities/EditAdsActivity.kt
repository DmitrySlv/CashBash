package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.utils.CityHelper
import com.dscreate_app.cashbash.utils.ImagePicker
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper
import com.dscreate_app.cashbash.utils.logD
import com.dscreate_app.cashbash.utils.showToast
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil

class EditAdsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        onClicks()
    }

    private fun init() = with(binding) {
    }

    private fun onClicks() = with(binding) {
        tvSelectCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@EditAdsActivity)
            dialog.showSpinnerDialog(this@EditAdsActivity, listCountry, tvSelectCountry)
            if (tvSelectCity.text.toString() != getString(R.string.select_city)) {
                tvSelectCity.text = getString(R.string.select_city)
            }
        }

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

        ibEditImage.setOnClickListener {
            ImagePicker.getImages(this@EditAdsActivity)
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
                    ImagePicker.getImages(this)
                } else {
                   showToast(getString(R.string.approve_permission_for_your_photo))
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //заменить RequestCode на свою константу
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES) {
            data?.let {
                val returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                logD(TAG, "Image :${returnValue?.get(0)}")
                logD(TAG, "Image :${returnValue?.get(1)}")
                logD(TAG, "Image :${returnValue?.get(2)}")
            }
        }
    }

    companion object {
        private const val TAG = "MyLog"
    }
}
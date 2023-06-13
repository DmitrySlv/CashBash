package com.dscreate_app.cashbash.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.utils.CityHelper
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper
import com.dscreate_app.cashbash.utils.showToast

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
    }
}
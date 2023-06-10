package com.dscreate_app.cashbash.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.utils.CityHelper
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper

class EditAdsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() = with(binding) {
        val listCountry = CityHelper.getAllCountries(this@EditAdsActivity)
        val dialog = DialogSpinnerHelper()
        dialog.showSpinnerDialog(this@EditAdsActivity, listCountry)
    }
}
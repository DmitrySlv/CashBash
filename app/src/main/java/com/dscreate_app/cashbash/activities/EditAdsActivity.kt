package com.dscreate_app.cashbash.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.utils.CityHelper
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper

class EditAdsActivity : AppCompatActivity() {

    val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        onClickSelectCountry()
    }

    private fun init() = with(binding) {
    }

    private fun onClickSelectCountry() = with(binding) {
        tvSelectCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@EditAdsActivity)
            dialog.showSpinnerDialog(this@EditAdsActivity, listCountry)
        }
    }
}
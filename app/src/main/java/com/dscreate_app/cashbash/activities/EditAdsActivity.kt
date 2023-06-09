package com.dscreate_app.cashbash.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding
import com.dscreate_app.cashbash.utils.CityHelper

class EditAdsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() = with(binding) {
        val arrayAdapter = ArrayAdapter(
            this@EditAdsActivity,
            android.R.layout.simple_spinner_item,
            CityHelper.getAllCountries(this@EditAdsActivity)
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCountry.adapter = arrayAdapter
    }
}
package com.dscreate_app.cashbash.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.ActivityEditAdsBinding

class EditAdsActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditAdsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}
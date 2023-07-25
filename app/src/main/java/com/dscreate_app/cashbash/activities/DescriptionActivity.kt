package com.dscreate_app.cashbash.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.databinding.ActivityDeascriptionBinding

class DescriptionActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDeascriptionBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}
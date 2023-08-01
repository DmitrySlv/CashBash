package com.dscreate_app.cashbash.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFilterBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolBar()
    }

    private fun initToolBar() = with(binding) {
        toolbar.setTitleTextColor(ContextCompat.getColor(this@FilterActivity, R.color.white))
        toolbar.setBackgroundColor(ContextCompat.getColor(this@FilterActivity, R.color.purple))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
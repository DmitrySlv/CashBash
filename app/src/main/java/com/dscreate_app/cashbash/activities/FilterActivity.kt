package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.ActivityFilterBinding
import com.dscreate_app.cashbash.utils.dialogs.CityHelper
import com.dscreate_app.cashbash.utils.dialogs.DialogSpinnerHelper
import com.dscreate_app.cashbash.utils.logD
import com.dscreate_app.cashbash.utils.showToast

class FilterActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFilterBinding.inflate(layoutInflater) }
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolBar()
        onClickSelectCountry()
        onClickSelectCity()
        onClickFilterDone()
        onClickFilterClear()
        getFilter()
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

    private fun onClickSelectCountry() = with(binding) {
        tvSelectCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountry, tvSelectCountry)
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
                    this@FilterActivity, selectedCountry
                )
                dialog.showSpinnerDialog(this@FilterActivity, listCity, tvSelectCity)
            } else {
                showToast(getString(R.string.no_select_country))
            }
        }
    }

    private fun onClickFilterDone() = with(binding) {
        btFilter.setOnClickListener {
           val intent = Intent().apply {
               putExtra(FILTER_KEY, createFiler())
           }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun onClickFilterClear() = with(binding) {
        btFilterClear.setOnClickListener {
            tvSelectCountry.text = getString(R.string.select_country)
            tvSelectCity.text = getString(R.string.select_city)
            edIndex.setText("")
            cbWithSend.isChecked = false
            setResult(RESULT_CANCELED)
        }
    }

    private fun getFilter() = with(binding){
        val filter = intent.getStringExtra(FILTER_KEY)
        if (filter != null && filter != EMPTY) {
            val filterList = filter.split(DELIMITERS)
            if (filterList[0] != EMPTY) {
                tvSelectCountry.text = filterList[0]
            }
            if (filterList[1] != EMPTY) {
                tvSelectCity.text = filterList[1]
            }
            if (filterList[2] != EMPTY) {
                edIndex.setText(filterList[2])
            }
            cbWithSend.isChecked = filterList[3].toBoolean()
        }
    }

    private fun createFiler(): String = with(binding) {
        val sBuilder = StringBuilder()
        val listTempFilter = listOf(
            tvSelectCountry.text,
            tvSelectCity.text,
            edIndex.text,
            cbWithSend.isChecked.toString()
            )
        for ((index, string) in listTempFilter.withIndex()) {
            if (string != getString(R.string.select_country) &&
                string != getString(R.string.select_city) &&
                string.isNotEmpty()) {
                sBuilder.append(string)
                if (index != listTempFilter.size - 1) sBuilder.append("_")
            } else {
                sBuilder.append(EMPTY)
                if (index != listTempFilter.size - 1) sBuilder.append("_")
            }
        }
        return sBuilder.toString()
    }

    companion object {
        private const val TAG = "MyLog"
        private const val EMPTY = "empty"
        const val FILTER_KEY = "filter_key"
        const val DELIMITERS = "_"
    }
}
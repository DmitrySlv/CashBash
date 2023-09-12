package com.dscreate_app.cashbash.utils.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscreate_app.cashbash.adapters.DialogSpinnerAdapter
import com.dscreate_app.cashbash.databinding.SpinnerLayoutBinding

class DialogSpinnerHelper {

    fun showSpinnerDialog(
        context: Context, list: ArrayList<String>, tvSelection: TextView
    ) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        val binding = SpinnerLayoutBinding.inflate(LayoutInflater.from(context))
        val spAdapter = DialogSpinnerAdapter(tvSelection, dialog)
        val rcView = binding.rcSpView
        val searchView = binding.svSpinner
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = spAdapter
        dialog.setView(binding.root)
        spAdapter.updateAdapter(list)
        setSearchView(spAdapter, list, searchView, context)
        dialog.show()
    }

    private fun setSearchView(
        spAdapter: DialogSpinnerAdapter, list: ArrayList<String>,
        searchView: SearchView, context: Context
    ) {
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempList = CityHelper.filterListData(list, newText, context)
                spAdapter.updateAdapter(tempList)
                return true
            }
        })
    }
}
package com.dscreate_app.cashbash.utils

import android.content.Context
import com.dscreate_app.cashbash.R
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

object CityHelper {

    fun getAllCountries(context: Context): ArrayList<String> {
        val tempArray = ArrayList<String>()

        try {
            val inputStream = context.assets.open(FILE_NAME)
            val size = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val countriesNames = jsonObject.names()
            countriesNames?.let {
                for (i in 0 until countriesNames.length()) {
                    tempArray.add(countriesNames.getString(i))
                }
            }

        } catch (e: IOException) {
        }
        return tempArray
    }

    fun getAllCities(context: Context, country: String): ArrayList<String> {
        val tempArray = ArrayList<String>()

        try {
            val inputStream = context.assets.open(FILE_NAME)
            val size = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val cityNames = jsonObject.getJSONArray(country)
                for (i in 0 until cityNames.length()) {
                    tempArray.add(cityNames.getString(i))
            }

        } catch (e: IOException) {
        }
        return tempArray
    }

    fun filterListData(
        list: ArrayList<String>, searchText: String?, context: Context
    ): ArrayList<String> {

        val tempList = ArrayList<String>()
        tempList.clear()
        if (searchText == null) {
            tempList.add(context.getString(R.string.no_result))
            return tempList
        }
        for (selection: String in list) {
            if (selection.lowercase().startsWith(searchText.lowercase())) {
                tempList.add(selection)
            }
        }
        if (tempList.size == 0){
            tempList.add(context.getString(R.string.no_result))
        }
        return tempList
    }


    private const val FILE_NAME = "countriesToCities.json"
}
package com.dscreate_app.cashbash.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException

object CityHelper {

    fun getAllCountries(context: Context): List<String> {
        val tempArray = mutableListOf<String>()

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

    private const val FILE_NAME = "countriesToCities.json"
}
package com.dscreate_app.cashbash.utils

import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

object ImagePicker {

    fun getImages(context: AppCompatActivity) {
        val options = Options.init()
            .setRequestCode(REQUEST_CODE_GET_IMAGES)
            .setCount(COUNT_IMAGES)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath(PATH_IMAGES)

        Pix.start(context, options)
    }

    const val REQUEST_CODE_GET_IMAGES = 999
    private const val COUNT_IMAGES = 3
    private const val PATH_IMAGES = "/pix/images"
}
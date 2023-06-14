package com.dscreate_app.cashbash.utils.image_picker

import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

object ImagePicker {

    fun getImages(context: AppCompatActivity, imageCounter: Int) {
        val options = Options.init()
            .setRequestCode(ImagePickerConst.REQUEST_CODE_GET_IMAGES)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath(ImagePickerConst.PATH_IMAGES)

        Pix.start(context, options)
    }
}
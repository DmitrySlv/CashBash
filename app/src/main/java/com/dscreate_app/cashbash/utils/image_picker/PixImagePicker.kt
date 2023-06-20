package com.dscreate_app.cashbash.utils.image_picker

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.activities.EditAdsActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PixImagePicker {

    fun getImages(context: AppCompatActivity, imageCounter: Int, rCode: Int) {
        val options = Options.init()
            .setRequestCode(rCode)
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath(ImageConst.PATH_IMAGES)

        Pix.start(context, options)
    }

    fun showSelectedImages(resultCode: Int, requestCode: Int, data: Intent?, edAct: EditAdsActivity) {
        if (resultCode == AppCompatActivity.RESULT_OK &&
            requestCode == ImageConst.REQUEST_CODE_GET_IMAGES) {

            data?.let {
                val returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                if (returnValue?.size!! > 1 && edAct.imageListFrag == null) {
                    edAct.openListImageFrag(returnValue)
                }
                if (returnValue.size == 1 && edAct.imageListFrag == null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        edAct.binding.pBarLoad.visibility = View.VISIBLE
                        val bitmapList = ImageManager.imageResize(returnValue)
                        edAct.binding.pBarLoad.visibility = View.GONE
                        edAct.imageAdapter.updateAdapter(bitmapList)
                    }
                }
                if (edAct.imageListFrag != null) {
                    edAct.imageListFrag?.updateAdapter(returnValue)
                }
            }
        }
        if (resultCode == AppCompatActivity.RESULT_OK &&
            requestCode == ImageConst.REQUEST_CODE_GET_SINGLE_IMAGE) {

            data?.let {
                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                uris?.let {
                    edAct.imageListFrag?.setSingeImage(uris[0], edAct.editImagePos)
                }
            }

        }
    }

}
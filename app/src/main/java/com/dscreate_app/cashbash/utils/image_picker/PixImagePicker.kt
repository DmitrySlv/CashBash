package com.dscreate_app.cashbash.utils.image_picker

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.EditAdsActivity
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PixImagePicker {

   private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = ImageConst.PATH_IMAGES
        }
        return options
    }

    fun launcher(
        edAct: EditAdsActivity, launcher: ActivityResultLauncher<Intent>?, imageCounter: Int
    ) {
        edAct.addPixToActivity(R.id.container, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    val fList = edAct.supportFragmentManager.fragments
                    fList.forEach {
                        if (it.isVisible) {
                            edAct.supportFragmentManager.beginTransaction()
                                .remove(it)
                                .commit()
                        }
                    }
                }
                else -> {}
            }
        }
    }



    fun getLauncherForSingleImage(edAct: EditAdsActivity): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
//            if (result.resultCode == AppCompatActivity.RESULT_OK) {
//
//                result.data?.let {
//                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
//                    uris?.let {
//                        edAct.imageListFrag?.setSingeImage(uris[0], edAct.editImagePos)
//                    }
//                }
//            }
        }
    }

    fun getLauncherForMultiSelectImages(edAct: EditAdsActivity): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
//            if (result.resultCode == AppCompatActivity.RESULT_OK) {
//
//                result.data?.let {
//                    val returnValue = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
//                    if (returnValue?.size!! > 1 && edAct.imageListFrag == null) {
//                        edAct.openListImageFrag(returnValue)
//                    }
//                    if (returnValue.size == 1 && edAct.imageListFrag == null) {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            edAct.binding.pBarLoad.visibility = View.VISIBLE
//                            val bitmapList = ImageManager.imageResize(returnValue)
//                            edAct.binding.pBarLoad.visibility = View.GONE
//                            edAct.imageAdapter.updateAdapter(bitmapList)
//                        }
//                    }
//                    if (edAct.imageListFrag != null) {
//                        edAct.imageListFrag?.updateAdapter(returnValue)
//                    }
//                }
//            }
        }
    }

}
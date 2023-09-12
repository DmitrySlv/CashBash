package com.dscreate_app.cashbash.utils.image_picker

import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
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

    fun getMultiImages(edAct: EditAdsActivity, imageCounter: Int) {
        edAct.addPixToActivity(R.id.container, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    multiSelectImages(edAct, result.data as MutableList<Uri>)
                }
                else -> {}
            }
        }
    }

    fun addImages(edAct: EditAdsActivity, imageCounter: Int) {
        edAct.addPixToActivity(R.id.container, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFrag(edAct)
                    edAct.imageListFrag?.updateAdapter(result.data as MutableList<Uri>, edAct)
                }
                else -> {}
            }
        }
    }

   private fun multiSelectImages(edAct: EditAdsActivity, uris: MutableList<Uri>) {
       if (uris.size > 1 && edAct.imageListFrag == null) {
           edAct.openListImageFrag(uris)
       }
       if (uris.size == 1 && edAct.imageListFrag == null) {
           CoroutineScope(Dispatchers.Main).launch {
               edAct.binding.pBarLoad.visibility = View.VISIBLE
               val bitmapList = ImageManager.imageResize(uris, edAct)
               edAct.binding.pBarLoad.visibility = View.GONE
               edAct.imageAdapter.updateAdapter(bitmapList)
               closePixFragment(edAct)
           }
       }
   }

    fun getSingleImage(edAct: EditAdsActivity) {
        edAct.addPixToActivity(R.id.container, getOptions(ImageConst.SINGLE_IMAGE)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFrag(edAct)
                    singleImage(edAct, result.data[0])
                }
                else -> {}
            }
        }
    }

    private fun openChooseImageFrag(edAct: EditAdsActivity) {
        edAct.imageListFrag?.let {
            edAct.supportFragmentManager.beginTransaction()
                .replace(R.id.container, it)
                .commit()
        }
    }

    private fun singleImage(edAct: EditAdsActivity, uri: Uri) {
        edAct.imageListFrag?.setSingeImage(uri, edAct.editImagePos)

    }

    private fun closePixFragment(edAct: EditAdsActivity) {
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible) {
                edAct.supportFragmentManager.beginTransaction()
                    .remove(it)
                    .commit()
            }
        }
    }
}
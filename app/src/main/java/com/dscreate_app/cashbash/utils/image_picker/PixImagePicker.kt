package com.dscreate_app.cashbash.utils.image_picker

import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dscreate_app.cashbash.activities.EditAdsActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object PixImagePicker {

   private fun getOptions(imageCounter: Int): Options {
        val options = Options.init()
            .setCount(imageCounter)
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
            .setPath(ImageConst.PATH_IMAGES)
        return options
    }

    fun launcher(
        edAct: EditAdsActivity, launcher: ActivityResultLauncher<Intent>?, imageCounter: Int
    ) {
        PermUtil.checkForCamaraWritePermissions(edAct) {
            val intent = Intent(edAct, Pix::class.java).apply {
                putExtra(ImageConst.OPTIONS, getOptions(imageCounter))
            }
            launcher?.launch(intent)
        }
    }



    fun getLauncherForSingleImage(edAct: EditAdsActivity): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                result.data?.let {
                    val uris = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    uris?.let {
                        edAct.imageListFrag?.setSingeImage(uris[0], edAct.editImagePos)
                    }
                }
            }
        }
    }

    fun getLauncherForMultiSelectImages(edAct: EditAdsActivity): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                result.data?.let {
                    val returnValue = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
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
        }
    }

}
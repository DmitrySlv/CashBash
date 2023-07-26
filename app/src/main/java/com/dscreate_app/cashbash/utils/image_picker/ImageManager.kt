package com.dscreate_app.cashbash.utils.image_picker

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageManager {

   private fun getImageSize(uri: Uri, act: Activity): List<Int> {
        val inStream = act.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inStream, null, options)
        return listOf(options.outWidth, options.outHeight)
    }

    fun chooseScaleType(imView: ImageView, bitmap: Bitmap) {
        if (bitmap.width > bitmap.height) {
            imView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    suspend fun imageResize(uris: MutableList<Uri>, act: Activity): MutableList<Bitmap> = withContext(Dispatchers.IO) {
        val tempList = mutableListOf<List<Int>>()
        val bitmapList = mutableListOf<Bitmap>()
        for (n in uris.indices) {
            val size = getImageSize(uris[n], act)
            val imageRatio =
                size[ImageConst.WIDTH].toFloat() / size[ImageConst.HEIGHT].toFloat()

            if (imageRatio > 1) {
                if (size[ImageConst.WIDTH] > ImageConst.MAX_IMAGE_SIZE) {
                    tempList.add(listOf(
                        ImageConst.MAX_IMAGE_SIZE, (ImageConst.MAX_IMAGE_SIZE / imageRatio).toInt())
                    )
                } else {
                    tempList.add(listOf(
                        size[ImageConst.HEIGHT], size[ImageConst.HEIGHT])
                    )
                }

            } else {
                if (size[ImageConst.HEIGHT] > ImageConst.MAX_IMAGE_SIZE) {
                    tempList.add(listOf(
                        ImageConst.MAX_IMAGE_SIZE, (ImageConst.MAX_IMAGE_SIZE * imageRatio).toInt())
                    )
                } else {
                    tempList.add(listOf(
                        size[ImageConst.HEIGHT], size[ImageConst.HEIGHT])
                    )
                }
            }
        }
            for (i in uris.indices) {
                kotlin.runCatching {
                    bitmapList.add(
                        Picasso.get().load(uris[i])
                            .resize(tempList[i][ImageConst.WIDTH], tempList[i][ImageConst.HEIGHT])
                            .get()
                    )
                }
               // Log.d(TAG, "Bitmap load done: ${e.isSuccess}")
        }

        return@withContext bitmapList
    }

    suspend fun getBitmapFromUris(uris: MutableList<String?>): List<Bitmap> = withContext(Dispatchers.IO) {
        val bitmapList = mutableListOf<Bitmap>()

        for (i in uris.indices) {
            kotlin.runCatching { bitmapList.add(Picasso.get().load(uris[i]).get()) }
        }
        return@withContext bitmapList
    }

    private const val TAG = "MyLog"
}
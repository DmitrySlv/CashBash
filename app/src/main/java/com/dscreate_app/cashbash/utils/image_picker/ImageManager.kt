package com.dscreate_app.cashbash.utils.image_picker

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream

object ImageManager {

   private fun getImageSize(uri: Uri, act: Activity): List<Int> {
        val inStream = act.contentResolver.openInputStream(uri)
        val fTemp = File(act.cacheDir, PATH_TO_FILE)
        if (inStream != null) {
           fTemp.copyInStreamToFile(inStream)
       }
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(fTemp.path, options)

        return if (imageRotation(fTemp) == ImageConst.IMAGE_ROTATE_90) {
             listOf(options.outHeight, options.outWidth)
        } else {
            listOf(options.outWidth, options.outHeight)
        }
    }

    private fun File.copyInStreamToFile(inStream: InputStream) {
        this.outputStream().use {
                out -> inStream.copyTo(out)
        }
    }

    private fun imageRotation(imageFile: File): Int {
        val rotation: Int
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        rotation = if (orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
            orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            ImageConst.IMAGE_ROTATE_90
        } else {
            ImageConst.IMAGE_ROTATE_0
        }
        return rotation
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
                val e = kotlin.runCatching {
                    bitmapList.add(
                        Picasso.get().load(uris[i])
                            .resize(tempList[i][ImageConst.WIDTH], tempList[i][ImageConst.HEIGHT])
                            .get()
                    )
                }
                Log.d(TAG, "Bitmap load done: ${e.isSuccess}")
        }

        return@withContext bitmapList
    }

    private const val TAG = "MyLog"
    private const val PATH_TO_FILE = "temp.tmp"
}
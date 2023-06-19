package com.dscreate_app.cashbash.utils.image_picker

import android.graphics.BitmapFactory
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File

object ImageManager {

    fun getImageSize(uri: String): List<Int> {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(uri, options)

        return if (imageRotation(uri) == ImageConst.IMAGE_ROTATE_90) {
             listOf(options.outHeight, options.outWidth)
        } else {
            listOf(options.outWidth, options.outHeight)
        }
    }

    private fun imageRotation(uri: String): Int {
        val rotation: Int
        val imageFile = File(uri)
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

    fun imageResize(uris: List<String>) {
        val tempList = mutableListOf<List<Int>>()
        for (n in uris.indices) {
            val size = getImageSize(uris[n])
            Log.d(TAG, "ширина: ${size[ImageConst.WIDTH]} высота: ${size[ImageConst.HEIGHT]}")
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
            Log.d(TAG, "ширина: ${tempList[n][ImageConst.WIDTH]} высота: ${tempList[n][ImageConst.HEIGHT]}")
        }
    }

    private const val TAG = "MyLog"
}
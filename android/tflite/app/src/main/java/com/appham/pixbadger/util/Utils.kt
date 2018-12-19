package com.appham.pixbadger.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.appham.pixbadger.model.ImgClassifier
import com.appham.pixbadger.model.ImgClassifierImpl.Companion.INPUT_SIZE
import java.io.File

object Utils {

    /**
     * @param file the image file
     * @return bitmap from file or null if not successful
     */
    fun loadImage(file: File): Bitmap? {
        Log.d(javaClass.name, "load image file: $file " + Thread.currentThread())
        return if (file.isFile) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    /**
     * @param bitmap of the image
     * @param startTime the time when processing of the image started
     * @param imageClassifier the ML image classifier
     * @param file the reference to the image file
     * @return an Img with recognized labels
     */
    fun recognizeImg(bitmap: Bitmap, startTime: Long, imageClassifier: ImgClassifier, file: File): ImgClassifier.Img{
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
        val resizeTime = System.currentTimeMillis() - startTime
        val img = imageClassifier.recognizeImage(scaledBitmap, file, resizeTime)
        Log.d(javaClass.name, img.toString())
        scaledBitmap.recycle()
        bitmap.recycle()
        return img
    }

}

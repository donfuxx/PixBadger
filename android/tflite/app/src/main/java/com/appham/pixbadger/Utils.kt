package com.appham.pixbadger

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.appham.pixbadger.TensorFlowImageClassifier.Companion.INPUT_SIZE
import java.io.File

object Utils {

    fun loadImage(file: File, imageClassifier: Classifier) {
        Log.d(javaClass.name, "load image file: $file " + Thread.currentThread())

        if (file.isFile) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)

            if (bitmap != null) {
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
                Log.d(javaClass.name, imageClassifier.recognizeImage(scaledBitmap, file).toString())
                scaledBitmap.recycle()
                bitmap.recycle()
            }
        }
    }

}

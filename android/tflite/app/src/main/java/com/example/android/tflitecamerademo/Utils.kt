package com.example.android.tflitecamerademo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

object Utils {

    const val IMG_SIZE = 224

    fun loadImage(file: File, imageClassifier: Classifier) {
        Log.d(javaClass.name, "load image file: $file " + Thread.currentThread())

        if (file.isFile) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMG_SIZE, IMG_SIZE, false)

            Log.d(javaClass.name, imageClassifier.recognizeImage(scaledBitmap).toString())

            scaledBitmap.recycle()
        }
    }

}

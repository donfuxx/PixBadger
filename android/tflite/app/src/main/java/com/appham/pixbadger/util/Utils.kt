package com.appham.pixbadger.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.appham.pixbadger.model.Img
import com.appham.pixbadger.model.ImgClassifierImpl.Companion.INPUT_SIZE
import com.appham.pixbadger.model.Times
import io.reactivex.subjects.PublishSubject
import java.io.File

object Utils {

    fun loadImage(file: File, imgSubject: PublishSubject<Pair<Bitmap, Img>>) {
        Log.d(javaClass.name, "load image file: $file " + Thread.currentThread())

        val startTime = System.currentTimeMillis()

        if (file.isFile) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)

            if (bitmap != null) {
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
                val resizeTime = System.currentTimeMillis() - startTime
//                Log.d(javaClass.name, imageClassifier.recognizeImage(scaledBitmap, file, resizeTime).toString())
//                scaledBitmap.recycle()
                imgSubject.onNext(Pair(scaledBitmap, Img(file, Times(0, resizeTime), mutableListOf())))
                bitmap.recycle()
            }
        }
    }

}

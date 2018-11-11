package com.appham.pixbadger.model

import android.graphics.Bitmap
import java.io.File

interface ImgClassifier {

    class Recognition(
            /**
             * A unique identifier for what has been recognized. Specific to the class, not the instance of
             * the object.
             */
            val id: String,
            /**
             * Display name for the recognition.
             */
            val title: String,
            /**
             * A sortable score for how good the recognition is relative to others. Higher should be better.
             */
            val confidence: Float) {

        override fun toString(): String {
            var resultString = ""
            resultString += "[$id] "

            resultString += "$title "

            resultString += String.format("(%.1f%%) ", confidence * 100.0f)

            return resultString.trim { it <= ' ' }
        }
    }

    fun recognizeImage(bitmap: Bitmap, file: File, resizeTime: Long): List<Recognition>
    fun close()
}

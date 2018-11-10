package com.appham.pixbadger

import android.graphics.Bitmap
import java.io.File

interface Classifier {

    class Recognition(
            /**
             * A unique identifier for what has been recognized. Specific to the class, not the instance of
             * the object.
             */
            val id: String?,
            /**
             * Display name for the recognition.
             */
            val title: String?,
            /**
             * A sortable score for how good the recognition is relative to others. Higher should be better.
             */
            val confidence: Float?,
            /**
             * The time required to classify the image
             */
            val time: String) {

        override fun toString(): String {
            var resultString = ""
            if (id != null) {
                resultString += "[$id] "
            }

            if (title != null) {
                resultString += "$title "
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f)
            }

            resultString += " $time"

            return resultString.trim { it <= ' ' }
        }
    }


    fun recognizeImage(bitmap: Bitmap, file: File): List<Recognition>

    fun close()
}

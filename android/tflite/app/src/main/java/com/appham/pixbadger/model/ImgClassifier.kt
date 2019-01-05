package com.appham.pixbadger.model

import android.content.Context
import android.graphics.Bitmap
import com.appham.pixbadger.R
import com.appham.pixbadger.util.getLabelTexts
import java.io.File

interface ImgClassifier {

    data class Img(val file: File,
                   val times: Times,
                   val recognition: List<ImgClassifier.Recognition>) {

        fun toString(context: Context): String = context.getString(R.string.time_values,
                times.imgClassifyTime,
                times.imgResizeTime,
                recognition.getLabelTexts())
    }

    data class Recognition(
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
            return ("[$id] $title " + String.format("(%.1f%%) ", confidence * 100.0f)).trim()
        }
    }

    fun recognizeImage(bitmap: Bitmap, file: File, resizeTime: Long): Img
    fun close()

    companion object {
        const val VERSION: Int = 3
    }
}

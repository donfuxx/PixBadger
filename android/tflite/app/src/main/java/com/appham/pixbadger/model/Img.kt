package com.appham.pixbadger.model

import android.content.Context
import com.appham.pixbadger.R
import com.appham.pixbadger.util.getLabelTexts
import java.io.File

data class Img(val file: File,
               val times: Times,
               var recognition: List<ImgClassifier.Recognition>) {

    fun toString(context: Context): String = context.getString(R.string.time_values,
            times.imgClassifyTime,
            times.imgResizeTime,
            recognition.getLabelTexts())
}
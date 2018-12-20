package com.appham.pixbadger.util

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.content.FileProvider
import android.util.Log
import com.appham.pixbadger.model.ImgClassifier
import com.appham.pixbadger.model.ImgClassifierImpl.Companion.INPUT_SIZE
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

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

    /**
     * @param assetManager
     * @param labelPath path to labels.txt file in assets folder
     * @return a list of strings with the labels from the assets folder
     */
    @Throws(IOException::class)
    fun loadLabelList(assetManager: AssetManager, labelPath: String): List<String> {
        val labelList = ArrayList<String>()
        val reader = BufferedReader(InputStreamReader(assetManager.open(labelPath)))
        val iterator = reader.lineSequence().iterator()
        while(iterator.hasNext()) {
            val line = iterator.next()
            labelList.add(line)
        }
        reader.close()
        return labelList
    }

    /**
     * Start an activity ACTION_VIEW intent for the provided file
     * @param context
     * @param file the file to open
     */
    fun openFileActivity(context: Context, file: File) {
        val intent = Intent(if (file.isDirectory) Intent.ACTION_GET_CONTENT else Intent.ACTION_VIEW)
        intent.data = FileProvider.getUriForFile(context,
                context.applicationContext.packageName,
                file)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

}

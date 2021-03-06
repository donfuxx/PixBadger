package com.appham.pixbadger.model

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.appham.pixbadger.util.SingletonHolder
import com.appham.pixbadger.util.Utils
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class ImgClassifierImpl private constructor(val context: Context) : ImgClassifier {

    private val interpreter: Interpreter by lazy {
        Interpreter(loadModelFile(context.assets, MODEL_PATH))
    }

    private val labelList: List<String> by lazy {
        Utils.loadLabelList(context.assets, LABEL_PATH)
    }

    override fun recognizeImage(bitmap: Bitmap, file: File, resizeTime: Long): ImgClassifier.Img {
        val byteBuffer = convertBitmapToByteBuffer(bitmap)
        val result = Array(1) { FloatArray(labelList.size) }

        val startTime = SystemClock.uptimeMillis()

        interpreter.run(byteBuffer, result)

        val times = Times(SystemClock.uptimeMillis() - startTime, resizeTime)

        Log.d(javaClass.name, "recognizeImage: ${times.imgClassifyTime} ms")

        return ImgClassifier.Img(file, times, getSortedRecognitions(result))
    }

    override fun close() {
        interpreter.close()
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * BATCH_SIZE * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val `val` = intValues[pixel++]
                byteBuffer.putFloat(((`val` shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }
        return byteBuffer
    }

    private fun getSortedRecognitions(labelProbArray: Array<FloatArray>): List<ImgClassifier.Recognition> {

        val pq = PriorityQueue(
                MAX_RESULTS,
                Comparator<ImgClassifier.Recognition> { lhs, rhs -> java.lang.Float.compare(rhs.confidence, lhs.confidence) })

        for (i in labelList.indices) {
            val confidence = labelProbArray[0][i]

            // Pass through 0.1 (10%) or more
            if (confidence > THRESHOLD) {
                pq.add(ImgClassifier.Recognition("" + i,
                        if (labelList.size > i) labelList[i] else "unknown",
                        confidence))
            }
        }

        val recognitions = ArrayList<ImgClassifier.Recognition>()
        val recognitionsSize = Math.min(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }

        return recognitions
    }

    companion object : SingletonHolder<ImgClassifierImpl, Context>(::ImgClassifierImpl) {
        private const val MAX_RESULTS = 3
        private const val BATCH_SIZE = 1
        private const val PIXEL_SIZE = 3
        private const val THRESHOLD = 0.1f
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128.0f
        private const val MODEL_PATH = "graph.lite"
        const val LABEL_PATH = "labels.txt"
        const val INPUT_SIZE = 224
    }

}
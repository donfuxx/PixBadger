package com.example.android.tflitecamerademo

import android.arch.lifecycle.MutableLiveData
import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class TensorFlowImageClassifier private constructor(private val context: Context) : Classifier {

    private val interpreter: Interpreter by lazy {
        Interpreter(loadModelFile(context.assets, MODEL_PATH))
    }

    private val labelList: List<String> by lazy {
        loadLabelList(context.assets, LABEL_PATH)
    }

    val lastRecognition: MutableLiveData<Img> = MutableLiveData()

    override fun recognizeImage(bitmap: Bitmap, imgPath: String): List<Classifier.Recognition> {
        val byteBuffer = convertBitmapToByteBuffer(bitmap)
        val result = Array(1) { FloatArray(labelList.size) }

        val startTime = SystemClock.uptimeMillis()

        interpreter.run(byteBuffer, result)

        val endTime = SystemClock.uptimeMillis()
        val runTime = (endTime - startTime).toString()

        Log.d(TAG, "recognizeImage: " + runTime + "ms")

        val recognitions = getSortedResult(result)
        val img = Img(imgPath, recognitions)
        lastRecognition.postValue(img)
        return recognitions
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

    @Throws(IOException::class)
    private fun loadLabelList(assetManager: AssetManager, labelPath: String): List<String> {
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

    private fun getSortedResult(labelProbArray: Array<FloatArray>): List<Classifier.Recognition> {

        val pq = PriorityQueue(
                MAX_RESULTS,
                Comparator<Classifier.Recognition> { lhs, rhs -> java.lang.Float.compare(rhs.confidence!!, lhs.confidence!!) })

        for (i in labelList.indices) {
            val confidence = labelProbArray[0][i] * 100 / 127.0f

            // Pass through 0.1 (10%) or more
            if (confidence > THRESHOLD) {
                pq.add(Classifier.Recognition("" + i,
                        if (labelList.size > i) labelList[i] else "unknown",
                        confidence))
            }
        }

        val recognitions = ArrayList<Classifier.Recognition>()
        val recognitionsSize = Math.min(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }

        return recognitions
    }

    companion object : SingletonHolder<TensorFlowImageClassifier, Context>(::TensorFlowImageClassifier) {
        private const val MAX_RESULTS = 3
        private const val BATCH_SIZE = 1
        private const val PIXEL_SIZE = 3
        private const val THRESHOLD = 0.1f
        private const val IMAGE_MEAN = 128
        private const val IMAGE_STD = 128.0f
        private const val MODEL_PATH = "graph.lite"
        private const val LABEL_PATH = "labels.txt"
        const val INPUT_SIZE = 224
    }

}
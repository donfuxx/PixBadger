package com.example.android.tflitecamerademo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Environment
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class ImageScanViewModel : ViewModel() {

    private var lastRecognition: MutableLiveData<Classifier.Recognition> = MutableLiveData()

    fun observeImgFiles(imageClassifier: TensorFlowImageClassifier): Disposable {
        lastRecognition = imageClassifier.lastRecognition
        return Observable.fromIterable(getImgFiles())
                .doOnNext { loadImage(it, imageClassifier) }
                .subscribeOn(Schedulers.computation())
                .subscribe()
    }

    fun getLatestImage(): LiveData<Classifier.Recognition> {
        return lastRecognition
    }

    private fun loadImage(it: File, imageClassifier: TensorFlowImageClassifier) {
        Utils.loadImage(it, imageClassifier)
    }

    private fun getImgFiles(): List<File> {
        val pixDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        Log.d(javaClass.name, "getImgFiles: ${pixDir.listFiles()[0].listFiles()}")
        return pixDir.listFiles()[0].listFiles().toList()
    }
}
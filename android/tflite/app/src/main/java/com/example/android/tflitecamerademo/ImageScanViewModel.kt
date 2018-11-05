package com.example.android.tflitecamerademo

import android.arch.lifecycle.ViewModel
import android.os.Environment
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class ImageScanViewModel : ViewModel() {

    fun observeImgFiles(imageClassifier: TensorFlowImageClassifier): Disposable {
        return Observable.fromIterable(getImgFiles())
                .doOnNext { Utils.loadImage(it, imageClassifier) }
                .subscribeOn(Schedulers.computation())
                .subscribe()
    }

    private fun getImgFiles(): List<File> {
        val pixDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        Log.d(javaClass.name, "getImgFiles: ${pixDir.listFiles()[0].listFiles()}")
        return pixDir.listFiles()[0].listFiles().toList()
    }
}
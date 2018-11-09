package com.example.android.tflitecamerademo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Environment
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

class ImageScanViewModel : ViewModel() {

    private var lastRecognition: MutableLiveData<Img> = MutableLiveData()

    fun observeImgFiles(imageClassifier: TensorFlowImageClassifier): Disposable {
        lastRecognition = imageClassifier.lastRecognition

        val imgSubject: PublishSubject<File> = PublishSubject.create<File>()

        val disposable = imgSubject.doOnNext { loadImage(it, imageClassifier) }
                .subscribeOn(Schedulers.computation())
                .subscribe()

        Completable.fromAction { postFiles(imgSubject, Environment.getExternalStorageDirectory()) }
                .subscribeOn(Schedulers.computation()).subscribe()

        return disposable
    }

    private fun postFiles(imgSubject: PublishSubject<File>, dir: File) {
        for (file in dir.listFiles()) {
            if (file.isDirectory) {
                postFiles(imgSubject, file)
            } else if (file.name.endsWith(".jpg")) {
                imgSubject.onNext(file)
            }
        }
    }

    fun getLatestImage(): LiveData<Img> {
        return lastRecognition
    }

    private fun loadImage(it: File, imageClassifier: TensorFlowImageClassifier) {
        Utils.loadImage(it, imageClassifier)
    }

}
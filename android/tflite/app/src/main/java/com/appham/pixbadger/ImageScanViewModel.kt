package com.appham.pixbadger

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Environment
import android.os.Trace
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

class ImageScanViewModel : ViewModel() {

    private var lastRecognition: MutableLiveData<Img> = MutableLiveData()

    private val endImgScanTime: MutableLiveData<Long> = MutableLiveData()

    private val disposables = CompositeDisposable()

    fun observeImgFiles(imageClassifier: TensorFlowImageClassifier) {
        lastRecognition = imageClassifier.lastRecognition

        Trace.beginSection("img-scan")
        val imgSubject: PublishSubject<File> = PublishSubject.create<File>()

        disposables.add(imgSubject.doOnNext { loadImage(it, imageClassifier) }
                .subscribeOn(Schedulers.computation())
                .subscribe())

        disposables.add(Completable.fromAction { postFiles(imgSubject, Environment.getExternalStorageDirectory()) }
                .subscribeOn(Schedulers.computation()).subscribe())
    }

    private fun postFiles(imgSubject: PublishSubject<File>, dir: File) {
        for (file in dir.listFiles()) {
            if (file.isDirectory) {
                postFiles(imgSubject, file)
            } else if (file.name.endsWith(".jpg")) {
                imgSubject.onNext(file)
                endImgScanTime.postValue(System.currentTimeMillis())
            }
        }

    }

    fun getLatestImage(): LiveData<Img> {
        return lastRecognition
    }

    fun getEndImgScan(): LiveData<Long> {
        return endImgScanTime
    }

    private fun loadImage(it: File, imageClassifier: TensorFlowImageClassifier) {
        Utils.loadImage(it, imageClassifier)
    }

    override fun onCleared() {
        disposables.clear()
    }
}
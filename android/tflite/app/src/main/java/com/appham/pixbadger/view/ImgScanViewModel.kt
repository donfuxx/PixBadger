package com.appham.pixbadger.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Environment
import com.appham.pixbadger.model.Img
import com.appham.pixbadger.model.ImgClassifierImpl
import com.appham.pixbadger.util.Utils
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

class ImgScanViewModel : ViewModel() {

    private var lastRecognition: MutableLiveData<Img> = MutableLiveData()

    private val endImgScanTime: MutableLiveData<Long> = MutableLiveData()

    private val disposables = CompositeDisposable()

    private val imgPattern: Regex by lazy {
        Regex("(?i).*(jpg|jpeg|png|bmp|gif|tiff)")
    }

    var isScanComplete: Boolean = false;

    fun observeImgFiles(imageClassifier: ImgClassifierImpl) {
        lastRecognition = imageClassifier.lastRecognition

        val imgSubject: PublishSubject<File> = PublishSubject.create<File>()

        disposables.add(Flowable.fromCallable { postFiles(imgSubject, Environment.getExternalStorageDirectory()) }
                .subscribeOn(Schedulers.computation())
                .subscribe())

        disposables.add(imgSubject.doOnNext { loadImage(it, imageClassifier) }
                .subscribeOn(Schedulers.computation())
                .doOnComplete { isScanComplete = true }
                .subscribe())

    }

    private fun postFiles(imgSubject: PublishSubject<File>, dir: File) {
        for (file in dir.listFiles()) {
            if (file.isDirectory) {
                postFiles(imgSubject, file)
            } else if (file.name.matches(imgPattern)) {
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

    private fun loadImage(it: File, imageClassifier: ImgClassifierImpl) {
        Utils.loadImage(it, imageClassifier)
    }

    override fun onCleared() {
        disposables.clear()
    }
}
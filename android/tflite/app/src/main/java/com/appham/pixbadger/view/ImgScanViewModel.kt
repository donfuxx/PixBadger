package com.appham.pixbadger.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.os.Environment
import com.appham.pixbadger.model.ImgClassifier
import com.appham.pixbadger.model.ImgClassifierImpl
import com.appham.pixbadger.util.Utils
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File

class ImgScanViewModel : ViewModel() {

    var isImgScanStarted: Boolean = false

    var isScanComplete: Boolean = false

    val imgList: MutableList<ImgClassifier.Img> = mutableListOf()

    val startImgScanTime: Long = System.currentTimeMillis()

    private val endImgScanTime: MutableLiveData<Long> = MutableLiveData()

    private var lastRecognition: MutableLiveData<ImgClassifier.Img> = MutableLiveData()

    private val disposables = CompositeDisposable()

    private val imgPattern: Regex by lazy {
        Regex("(?i).*(jpg|jpeg|png|bmp|gif|tiff)")
    }

    private val imgListObserver: Observer<ImgClassifier.Img> by lazy {
        Observer<ImgClassifier.Img> {
            it?.let {
                imgList.add(it)
            }
        }
    }

    fun observeImgFiles(imageClassifier: ImgClassifierImpl) {

        // only start image scan once
        if (isImgScanStarted) {
            return
        }

        lastRecognition.observeForever(imgListObserver)

        val imgSubject: PublishSubject<File> = PublishSubject.create<File>()

        disposables.add(Flowable.fromCallable { postFiles(imgSubject, Environment.getExternalStorageDirectory()) }
                .subscribeOn(Schedulers.computation())
                .subscribe())

        disposables.add(imgSubject.doOnNext { processImage(it, imageClassifier) }
                .subscribeOn(Schedulers.computation())
                .doOnComplete { isScanComplete = true }
                .subscribe())

        isImgScanStarted = true

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

    fun getLatestImage(): LiveData<ImgClassifier.Img> {
        return lastRecognition
    }

    fun getEndImgScan(): LiveData<Long> {
        return endImgScanTime
    }

    private fun processImage(file: File, imageClassifier: ImgClassifierImpl) {
        val startTime = System.currentTimeMillis()
        Utils.loadImage(file)?.let {
            val img = Utils.recognizeImg(it, startTime, imageClassifier, file)
            lastRecognition.postValue(img)
        }
    }

    override fun onCleared() {
        disposables.clear()
        lastRecognition.removeObserver(imgListObserver)
    }
}
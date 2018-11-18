package com.appham.pixbadger.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.appham.pixbadger.model.Img
import com.appham.pixbadger.model.ImgClassifierImpl
import com.appham.pixbadger.util.Utils
import io.reactivex.Completable
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

    fun observeImgFiles(imageClassifier: ImgClassifierImpl) {
        lastRecognition = imageClassifier.lastRecognition

        val fileSubject: PublishSubject<File> = PublishSubject.create<File>()

        disposables.add(Completable.fromAction { postFiles(fileSubject, Environment.getExternalStorageDirectory()) }
                .subscribeOn(Schedulers.computation())
                .subscribe())

        val imgSubject: PublishSubject<Pair<Bitmap, Img>> = PublishSubject.create<Pair<Bitmap, Img>>()

        disposables.add(fileSubject.doOnNext { loadImage(it, imgSubject) }
                .subscribeOn(Schedulers.newThread())
                .subscribe())

        disposables.add(imgSubject.doOnNext { classifyImage(it, imageClassifier) }
                .subscribeOn(Schedulers.newThread())
                .subscribe())

    }

    private fun classifyImage(img: Pair<Bitmap, Img>, imageClassifier: ImgClassifierImpl) {
        Log.d(javaClass.name, imageClassifier.recognizeImage(img).toString())
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

    private fun loadImage(it: File, imgSubject: PublishSubject<Pair<Bitmap, Img>>) {
        Utils.loadImage(it, imgSubject)
    }

    override fun onCleared() {
        disposables.clear()
    }
}
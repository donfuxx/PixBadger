package com.appham.pixbadger.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.persistence.room.Room
import android.os.Environment
import android.util.Log
import com.appham.pixbadger.model.ImgClassifierImpl
import com.appham.pixbadger.model.db.ImgDataBase
import com.appham.pixbadger.model.db.ImgEntity
import com.appham.pixbadger.util.Utils
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.util.concurrent.Executors

class ImgScanViewModel : ViewModel() {

    var isImgScanStarted: Boolean = false

    var isScanComplete: Boolean = false

    val imgList: MutableList<ImgEntity> = mutableListOf()

    val startImgScanTime: Long = System.currentTimeMillis()

    private val endImgScanTime: MutableLiveData<Long> = MutableLiveData()

    private var lastRecognition: MutableLiveData<ImgEntity> = MutableLiveData()

    private val disposables = CompositeDisposable()

    private val imgPattern: Regex by lazy {
        Regex("(?i).*(jpg|jpeg|png|bmp|gif|tiff)")
    }

    private val imgListObserver: Observer<ImgEntity> by lazy {
        Observer<ImgEntity> {
            it?.let {
                imgList.add(it)
            }
        }
    }

    private lateinit var db: ImgDataBase

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    fun observeImgFiles(imageClassifier: ImgClassifierImpl) {

        // only start image scan once
        if (isImgScanStarted) {
            return
        }

        // setup db
        db = Room.databaseBuilder(
                imageClassifier.context.applicationContext,
                ImgDataBase::class.java, "img_database"
        ).fallbackToDestructiveMigration().build()

        // add images from db to img list
        initImgList()

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

    fun initImgList(label: String) {
        executor.execute {
            db.imgDao().getImgs(label)?.let {
                imgList.clear()
                imgList.addAll(it)
            }
        }
    }

    private fun initImgList() {
        executor.execute {
            imgList.clear()
            imgList.addAll(db.imgDao().getAll())
        }
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

    fun getLatestImage(): LiveData<ImgEntity> {
        return lastRecognition
    }

    fun getEndImgScan(): LiveData<Long> {
        return endImgScanTime
    }

    private fun processImage(file: File, imageClassifier: ImgClassifierImpl) {

        // skip already classified images
        db.imgDao().getImg(file.absolutePath)?.let {
            if (it.fileSize == file.length()) {
                Log.d(javaClass.name, "Skip classify: Img already classified before: $it")
                return
            }
        }

        // classify the new images
        val startTime = System.currentTimeMillis()
        Utils.loadImage(file)?.let {
            val img = Utils.recognizeImg(it, startTime, imageClassifier, file)
            lastRecognition.postValue(ImgEntity.from(img))

            db.imgDao().insert(ImgEntity.from(img))
        }
    }

    override fun onCleared() {
        disposables.clear()
        lastRecognition.removeObserver(imgListObserver)
    }
}
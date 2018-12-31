package com.appham.pixbadger.view

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.persistence.room.Room
import android.os.Environment
import android.util.Log
import com.appham.pixbadger.model.ImgClassifier
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

class ImgScanViewModel(application: Application) : AndroidViewModel(application) {

    var isImgScanStarted: Boolean = false

    var isScanComplete: Boolean = false

    var imgList: MutableList<ImgEntity> = mutableListOf()

    val startImgScanTime: Long = System.currentTimeMillis()

    var isPaused = false

    var label: String? = null

    lateinit var labelList: List<String>

    private val endImgScanTime: MutableLiveData<Long> = MutableLiveData()

    private val lastRecognition: MutableLiveData<ImgEntity> = MutableLiveData()

    private val labels: MutableLiveData<List<Pair<String, Int>>> = MutableLiveData()

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

    private val db: ImgDataBase by lazy {
        Room.databaseBuilder(
                application,
                ImgDataBase::class.java, "img_database"
        ).fallbackToDestructiveMigration().build()
    }

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    fun observeImgFiles(imageClassifier: ImgClassifierImpl) {

        // only start image scan once
        if (isImgScanStarted) {
            return
        }

        // add images from db to img list
        initImgList()

        lastRecognition.observeForever(imgListObserver)

        val imgSubject: PublishSubject<File> = PublishSubject.create<File>()

        disposables.add(Flowable.fromCallable { postFiles(imgSubject, Environment.getExternalStorageDirectory()) }
                .subscribeOn(Schedulers.computation())
                .subscribe())

        disposables.add(imgSubject.doOnNext { processImage(it, imageClassifier) }
                .subscribeOn(Schedulers.computation())
                .doOnComplete { onImgScanComplete() }
                .subscribe())

        isImgScanStarted = true

    }

    fun getLatestImage(): LiveData<ImgEntity> {
        return lastRecognition
    }

    fun getEndImgScan(): LiveData<Long> {
        return endImgScanTime
    }

    fun getLabels(): LiveData<List<Pair<String, Int>>> {
        return labels
    }

    fun initImgList(label: String) {
        executor.execute {
            db.imgDao().getImgs(label)?.let {
                imgList.clear()
                imgList.addAll(it)
            }
        }
    }

    fun initImgList() {
        executor.execute {
            imgList = mutableListOf()
            imgList.addAll(db.imgDao().getAll())
            updateLabels()
        }
    }

    private fun updateLabels() {
        executor.execute {
            val labelItemList = mutableListOf<Pair<String, Int>>()
            for (label in labelList) {
                labelItemList.add(Pair(label, db.imgDao().getLabelFacet(label)))
            }
            Log.d(javaClass.name, "labelItemList $labelItemList")
            labels.postValue(labelItemList)
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

    private fun onImgScanComplete() {
        isScanComplete = true
        updateLabels()
    }

    private fun processImage(file: File, imageClassifier: ImgClassifierImpl) {

        // skip already classified images
        db.imgDao().getImg(file.absolutePath)?.let {
            if (it.fileSize == file.length() && it.classifierVersion == ImgClassifier.VERSION) {
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
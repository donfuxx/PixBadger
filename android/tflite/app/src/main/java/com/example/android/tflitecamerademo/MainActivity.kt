package com.example.android.tflitecamerademo

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.support.v4.app.ActivityCompat
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File


class MainActivity : Activity() {

    companion object {
        const val MODEL_PATH = "graph.lite"
        const val LABEL_PATH = "labels.txt"
        const val INPUT_SIZE = 224
    }

    private val imageClassifier by lazy {
        TensorFlowImageClassifier.create(
                assets,
                MODEL_PATH,
                LABEL_PATH,
                INPUT_SIZE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isStoragePermissionGranted()) {
            Log.v(javaClass.name,"Permission is granted")
            observeImgFiles()
        }
    }

    private fun observeImgFiles() {
        Observable.fromIterable(getImgFiles())
                .doOnNext { Utils.loadImage(it, imageClassifier) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    fun getImgFiles(): List<File> {
        val pixDir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
        Log.d(javaClass.name, "getImgFiles: ${pixDir.listFiles()[0].listFiles()}")
        return pixDir.listFiles()[0].listFiles().toList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(javaClass.name, "Permission: " + permissions[0] + "was " + grantResults[0])
            //resume tasks needing this permission
            observeImgFiles()
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(javaClass.name, "Permission is granted")
                true
            } else {

                Log.v(javaClass.name, "Permission is revoked")
                ActivityCompat.requestPermissions(this,
                        arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(javaClass.name, "Permission is granted")
            true
        }
    }

}
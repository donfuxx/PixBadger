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

    private val imageClassifier by lazy {
        TensorFlowImageClassifier.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isStoragePermissionGranted()) {
            observeImgFiles()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_RC && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(javaClass.name, "Permission: " + permissions[0] + "was " + grantResults[0])
            observeImgFiles()
        }
    }

    private fun observeImgFiles() {
        Observable.fromIterable(getImgFiles())
                .doOnNext { Utils.loadImage(it, imageClassifier) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun getImgFiles(): List<File> {
        val pixDir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
        Log.d(javaClass.name, "getImgFiles: ${pixDir.listFiles()[0].listFiles()}")
        return pixDir.listFiles()[0].listFiles().toList()
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(javaClass.name, "Storage permission is granted")
                true
            } else {
                Log.v(javaClass.name, "Storage permission is revoked")
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_RC)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(javaClass.name, "Storage permission is granted")
            true
        }
    }

    companion object {
        const val STORAGE_PERMISSION_RC = 0
    }

}
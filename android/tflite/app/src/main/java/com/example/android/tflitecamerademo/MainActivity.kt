package com.example.android.tflitecamerademo

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {

    private val imageClassifier by lazy {
        TensorFlowImageClassifier.getInstance(this)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ImageScanViewModel::class.java)
    }

    private lateinit var imgFilesDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(ImgListFragment(), R.id.frameImgList)

        if (isStoragePermissionGranted()) {
            imgFilesDisposable = viewModel.observeImgFiles(imageClassifier)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_RC && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(javaClass.name, "Permission: " + permissions[0] + "was " + grantResults[0])
            imgFilesDisposable = viewModel.observeImgFiles(imageClassifier)
        }
    }

    override fun onDestroy() {
        imgFilesDisposable.takeIf { !it.isDisposed }?.apply { dispose() }
        super.onDestroy()
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
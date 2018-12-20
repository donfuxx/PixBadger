package com.appham.pixbadger.view

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.appham.pixbadger.R
import com.appham.pixbadger.model.ImgClassifierImpl
import com.appham.pixbadger.util.Utils
import com.appham.pixbadger.util.replaceFragment

class MainActivity : AppCompatActivity() {

    private val navigationView by lazy {
        findViewById<NavigationView>(R.id.navView)
    }

    private val drawerLayout by lazy {
        findViewById<DrawerLayout>(R.id.drawerLayout)
    }

    private val imageClassifier by lazy {
        ImgClassifierImpl.getInstance(this)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ImgScanViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(android.R.drawable.ic_menu_more)
        }

        viewModel.labelList = Utils.loadLabelList(assets, ImgClassifierImpl.LABEL_PATH)

        viewModel.getLabels().observe(this, Observer {
            it?.let {
                navigationView.menu.removeGroup(R.id.group_filter)

                for (label in it) {
                    navigationView.menu.add(R.id.group_filter,
                            Menu.NONE,
                            Menu.NONE,
                            "${label.first} (${label.second})")
                }
            }
        })

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isCheckable = true
            menuItem.isChecked = true

            when (menuItem.itemId) {
                R.id.action_menu_all -> replaceFragment(ImgListFragment.getNewInstance(), R.id.frameImgList)
                else -> replaceFragment(ImgListFragment.getNewInstance(
                        menuItem.title.toString().replace(Regex("\\s+.*"), "")),
                        R.id.frameImgList)
            }

            drawerLayout.closeDrawers()

            true
        }

        replaceFragment(ImgListFragment.getNewInstance(), R.id.frameImgList)

        if (isStoragePermissionGranted()) {
            viewModel.observeImgFiles(imageClassifier)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_RC && !grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(javaClass.name, "Permission: " + permissions[0] + "was " + grantResults[0])
            viewModel.observeImgFiles(imageClassifier)
        }
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
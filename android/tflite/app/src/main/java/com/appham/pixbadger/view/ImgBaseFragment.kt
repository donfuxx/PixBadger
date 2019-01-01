package com.appham.pixbadger.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.appham.pixbadger.R
import com.appham.pixbadger.model.db.ImgEntity
import com.appham.pixbadger.util.Utils
import com.appham.pixbadger.util.replaceFragment
import java.io.File

abstract class ImgBaseFragment : Fragment() {

    protected val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ImgScanViewModel::class.java)
    }

    protected val parentActivity by lazy {
        activity as AppCompatActivity?
    }

    protected val imgObserver: Observer<ImgEntity> by lazy {
        Observer<ImgEntity> { imgEntity ->
            imgEntity?.let {
                Log.d(this.javaClass.name, "image observed: $it")
                val position = viewModel.imgAdapter.images.size - 1
                viewModel.imgAdapter.notifyItemChanged(position)
                if (!viewModel.isPaused) {
                    imgList.scrollToPosition(position)
                }

                parentActivity?.supportActionBar?.title = "${viewModel.imgAdapter.images.size} images classified"
            }
        }
    }

    protected lateinit var imgList: RecyclerView

    //region lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.imgAdapter.itemLayout = if (this is ImgGridFragment) R.layout.item_grid_img else R.layout.item_list_img
        viewModel.isScanComplete().observe(this, Observer {
            viewModel.imgAdapter.notifyDataSetChanged()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // get recycler-list of img results
        imgList = view.findViewById(R.id.listImgs)
        imgList.setHasFixedSize(true)

        // filter by label if provided as arg or show all images
        initImgs()

        imgList.adapter = viewModel.imgAdapter

        viewModel.getLatestImage().observeForever(imgObserver)

        viewModel.getEndImgScan().observe(this, Observer { endImgScanTime ->
            endImgScanTime?.let {
                val elapsedTime = it - viewModel.startImgScanTime
                if (!viewModel.imgAdapter.images.isEmpty()) {
                    val timePerImg = elapsedTime / viewModel.imgAdapter.images.size
                    parentActivity?.supportActionBar?.subtitle = "in $elapsedTime ms - $timePerImg ms per image"
                }
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_img_list, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)

        val viewTypeItem = menu?.findItem(R.id.action_menu_grid_view)
        if (this is ImgGridFragment) {
            viewTypeItem?.setIcon(android.R.drawable.ic_menu_add)
        } else {
            viewTypeItem?.setIcon(android.R.drawable.ic_menu_gallery)
        }

        val pausedItem = menu?.findItem(R.id.action_menu_pause)
        if (viewModel.isPaused) {
            pausedItem?.setIcon(android.R.drawable.ic_media_play)
        } else {
            pausedItem?.setIcon(android.R.drawable.ic_media_pause)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_menu_grid_view -> toggleGridView()
            R.id.action_menu_pause -> togglePause(item)
            R.id.action_menu_open_folder -> openFolder()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.getLatestImage().removeObserver(imgObserver)
    }
    //endregion lifecycle methods

    protected fun initImgs() {
        viewModel.label?.let {
            viewModel.initImgList(it)
        } ?: viewModel.initImgList()
    }

    private fun toggleGridView() {
        viewModel.isListView = !viewModel.isListView
        parentActivity?.replaceFragment(getNewInstance(viewModel), R.id.frameImgList)
    }

    private fun togglePause(item: MenuItem) {
        viewModel.isPaused = !viewModel.isPaused
        item.setIcon(if (viewModel.isPaused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause)
    }

    private fun openFolder() {
        if (!viewModel.imgAdapter.images.isEmpty()) {
            Utils.openFileActivity(parentActivity!!, File(viewModel.imgAdapter.images[0].path).parentFile)
        }
    }

    companion object {

        fun getNewInstance(viewModel: ImgScanViewModel): ImgBaseFragment {
            return if (viewModel.isListView) {
                ImgListFragment()
            } else {
                ImgGridFragment()
            }
        }
    }

}
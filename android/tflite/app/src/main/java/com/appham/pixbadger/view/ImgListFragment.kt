package com.appham.pixbadger.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appham.pixbadger.R
import com.appham.pixbadger.model.Img

class ImgListFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ImgScanViewModel::class.java)
    }

    private val imgAdapter by lazy {
        ImgAdapter(parentActivity!!, mutableListOf())
    }

    private val parentActivity by lazy {
        activity as AppCompatActivity?
    }

    private var isPaused = false

    private lateinit var imgObserver: Observer<Img>

    //region lifecycle methods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_img_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // get recycler-list of ad results
        val imgList: RecyclerView = view.findViewById(R.id.listMarks)
        imgList.setHasFixedSize(true)

        // use a linear layout manager
        val imgLayoutManager = LinearLayoutManager(activity)
        imgList.layoutManager = imgLayoutManager

        imgList.adapter = imgAdapter

        val startImgScanTime = System.currentTimeMillis()

        imgObserver = Observer { img ->
            img?.let {
                Log.d(this.javaClass.name, "image observed: $it")
                imgAdapter.images.add(it)
            }.let {
                val position = imgAdapter.images.size - 1
                imgAdapter.notifyItemChanged(position)
                if (!isPaused) {
                    imgList.scrollToPosition(position)
                }

                parentActivity?.supportActionBar?.title = "${imgAdapter.images.size} images classified"
            }
        }

        viewModel.getLatestImage().observeForever(imgObserver)

        viewModel.getEndImgScan().observe(this, Observer { endImgScanTime ->
            endImgScanTime?.let {
                val elapsedTime = it - startImgScanTime
                val timePerImg = elapsedTime / imgAdapter.images.size
                parentActivity?.supportActionBar?.subtitle = "in $elapsedTime ms - $timePerImg ms per image"
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isScanComplete) {
            imgAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.getLatestImage().removeObserver(imgObserver)
    }
//endregion
}
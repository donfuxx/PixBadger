package com.appham.pixbadger.view

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appham.pixbadger.R

class ImgListFragment : ImgBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_img_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // get recycler-list of img results
        imgList = view.findViewById(R.id.listImgs)
        imgList.setHasFixedSize(true)

        // use a linear layout manager
        imgList.layoutManager = LinearLayoutManager(activity)

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
}
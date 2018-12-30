package com.appham.pixbadger.view

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appham.pixbadger.R

class ImgGridFragment : ImgBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_img_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // get recycler-list of img results
        imgList = view.findViewById(R.id.listImgs)
        imgList.setHasFixedSize(true)

        // use a linear layout manager
        imgList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // filter by label if provided as arg or show all images
        initImgs()

        imgList.adapter = imgAdapter

        viewModel.getLatestImage().observeForever(imgObserver)

        viewModel.getEndImgScan().observe(this, Observer { endImgScanTime ->
            endImgScanTime?.let {
                val elapsedTime = it - viewModel.startImgScanTime
                if (!imgAdapter.images.isEmpty()) {
                    val timePerImg = elapsedTime / imgAdapter.images.size
                    parentActivity?.supportActionBar?.subtitle = "in $elapsedTime ms - $timePerImg ms per image"
                }
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        fun getNewInstance(): ImgGridFragment {
            return ImgGridFragment()
        }

        fun getNewInstance(label: String): ImgGridFragment {
            val fragment = ImgGridFragment()
            val bundle = Bundle()
            bundle.putString(ARG_LABEL, label)
            fragment.arguments = bundle
            return fragment
        }
    }
}
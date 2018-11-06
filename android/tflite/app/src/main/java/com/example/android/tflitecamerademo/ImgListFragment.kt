package com.example.android.tflitecamerademo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ImgListFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ImageScanViewModel::class.java)
    }

    val imgAdapter by lazy {
        ImgAdapter(mutableListOf())
    }

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

        viewModel.getLatestImage().observe(this, Observer { recognition ->
            recognition?.let {imgAdapter.images.add(it)}.let { imgAdapter.notifyDataSetChanged() }
        })

        super.onViewCreated(view, savedInstanceState)
    }
    //endregion
}
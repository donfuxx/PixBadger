package com.appham.pixbadger.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.appham.pixbadger.R
import com.appham.pixbadger.model.db.ImgEntity

class ImgListFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity!!).get(ImgScanViewModel::class.java)
    }

    private val imgAdapter by lazy {
        ImgAdapter(parentActivity!!, viewModel.imgList)
    }

    private val parentActivity by lazy {
        activity as AppCompatActivity?
    }

    private var isPaused = false

    private val imgObserver: Observer<ImgEntity> by lazy {
        Observer<ImgEntity> { imgEntity ->
            imgEntity?.let {
                Log.d(this.javaClass.name, "image observed: $it")
                val position = imgAdapter.images.size - 1
                imgAdapter.notifyItemChanged(position)
                if (!isPaused) {
                    imgList.scrollToPosition(position)
                }

                parentActivity?.supportActionBar?.title = "${imgAdapter.images.size} images classified"
            }
        }
    }

    private lateinit var imgList: RecyclerView

    //region lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
        arguments?.let {
            viewModel.initImgList(it.getString(ARG_LABEL))
        } ?: viewModel.initImgList()


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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_img_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_menu_pause -> togglePause(item)
        }
        return super.onOptionsItemSelected(item)
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
    //endregion lifecycle methods

    private fun togglePause(item: MenuItem) {
        isPaused = !isPaused
        item.setIcon(if (isPaused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause)
        if (viewModel.isScanComplete) {
            imgAdapter.notifyDataSetChanged()
        }
    }

    companion object {

        const val ARG_LABEL = "label"

        fun getNewInstance(): ImgListFragment {
            return ImgListFragment()
        }

        fun getNewInstance(label: String): ImgListFragment {
            val fragment = ImgListFragment()
            val bundle = Bundle()
            bundle.putString(ARG_LABEL, label)
            fragment.arguments = bundle
            return fragment
        }
    }
}
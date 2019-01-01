package com.appham.pixbadger.view

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.appham.pixbadger.R
import com.appham.pixbadger.model.db.ImgEntity
import com.appham.pixbadger.util.Utils
import com.squareup.picasso.Picasso
import java.io.File

class ImgAdapter(private val context: Context,
                 val images: MutableList<ImgEntity>) : RecyclerView.Adapter<ImgHolder>() {

    var itemLayout: Int = R.layout.item_list_img

    private val screenWidthPx by lazy { Resources.getSystem().displayMetrics.widthPixels }

    private val screenHeightPx by lazy { Resources.getSystem().displayMetrics.heightPixels }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgHolder {
        return ImgHolder(LayoutInflater.from(parent.context)
                .inflate(itemLayout, parent, false))
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImgHolder, position: Int) {

        images.takeIf { position < it.size }?.get(position)?.let { imgEntity ->
            holder.txtLabel.text = if (itemLayout == R.layout.item_list_img) {
                imgEntity.toString(context)
            } else {
                imgEntity.labels
            }

            val file = File(imgEntity.path)
            Picasso.get().load(file)
                    .resize(Math.min(screenWidthPx / 2, screenHeightPx / 2),
                            Math.min(screenWidthPx / 4, screenHeightPx / 3))
                    .onlyScaleDown()
                    .centerInside()
                    .placeholder(R.drawable.ic_launcher)
                    .into(holder.imgItem)

            // add click listener to open image after click
            (holder.imgItem.parent as ViewGroup).setOnClickListener {
                Utils.openFileActivity(context, file)
            }

        }

    }
}
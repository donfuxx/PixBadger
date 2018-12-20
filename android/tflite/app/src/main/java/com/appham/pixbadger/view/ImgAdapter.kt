package com.appham.pixbadger.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.appham.pixbadger.R
import com.appham.pixbadger.model.db.ImgEntity
import com.squareup.picasso.Picasso
import java.io.File

class ImgAdapter(private val context: Context, val images: MutableList<ImgEntity>) : RecyclerView.Adapter<ImgHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgHolder {
        return ImgHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_img, parent, false))
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImgHolder, position: Int) {

        images.takeIf { position < it.size }?.get(position)?.let {
            holder.txtLabel.text = it.toString(context)

            Picasso.get().load(File(it.path))
                    .resize(100, 100)
                    .onlyScaleDown()
                    .centerInside()
                    .placeholder(R.drawable.ic_launcher)
                    .into(holder.imgItem)
        }

    }
}
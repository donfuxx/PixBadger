package com.appham.pixbadger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.squareup.picasso.Picasso

class ImgAdapter(val images: MutableList<Img>) : RecyclerView.Adapter<ImgHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgHolder {
        return ImgHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_img, parent, false))
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImgHolder, position: Int) {

        images.takeIf { position < it.size }?.get(position)?.let {
            holder.txtLabel.text = it.recognition.getLabelTexts()

            Picasso.get().load(it.file)
                    .resize(100, 100)
                    .onlyScaleDown()
                    .centerInside()
                    .placeholder(R.drawable.ic_launcher)
                    .into(holder.imgItem)
        }

    }
}
package com.example.android.tflitecamerademo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ImgAdapter(val images: MutableList<Classifier.Recognition>) : RecyclerView.Adapter<ImgHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgHolder {
        return ImgHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_img, parent, false))
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ImgHolder, position: Int) {
        holder.txtLabel.text = images.takeIf { position < it.size}?.get(position)?.title ?: "unknown"
    }
}
package com.appham.pixbadger.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.appham.pixbadger.R

class ImgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imgItem: ImageView = itemView.findViewById(R.id.imgItem)
    val txtLabel: TextView = itemView.findViewById(R.id.txtLabel)
}
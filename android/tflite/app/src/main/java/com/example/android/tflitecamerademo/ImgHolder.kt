package com.example.android.tflitecamerademo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ImgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val txtLabel: TextView = itemView.findViewById(R.id.txtLabel)
}
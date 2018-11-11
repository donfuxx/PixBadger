package com.appham.pixbadger.util

import com.appham.pixbadger.model.ImgClassifier

fun List<ImgClassifier.Recognition>.getLabelTexts():String {
    val labelText = StringBuilder()
    for (recognition in this) {
        labelText.append(recognition.title)
                .append(": ")
                .append(String.format("(%.1f%%) ", recognition.confidence?.times(100.0f)))
                .append("\n")
    }
    return labelText.toString()
}
package com.appham.pixbadger

fun List<Classifier.Recognition>.getLabelTexts():String {
    val labelText = StringBuilder()
    labelText.append("recognition time: ")
            .append("${this[0].time} ms")
            .append("\n")
    for (recognition in this) {
        labelText.append(recognition.title)
                .append(": ")
                .append(String.format("(%.1f%%) ", recognition.confidence?.times(100.0f)))
                .append("\n")
    }
    return labelText.toString()
}
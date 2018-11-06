package com.example.android.tflitecamerademo

import java.io.File

data class Img(val file: File, val recognition: List<Classifier.Recognition>)
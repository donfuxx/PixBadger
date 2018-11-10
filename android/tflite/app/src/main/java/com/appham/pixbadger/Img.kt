package com.appham.pixbadger

import java.io.File

data class Img(val file: File, val recognition: List<Classifier.Recognition>)
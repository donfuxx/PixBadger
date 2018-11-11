package com.appham.pixbadger.model

import java.io.File

data class Img(val file: File,
               val times: Times,
               val recognition: List<ImgClassifier.Recognition>)
package com.appham.pixbadger.model.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.appham.pixbadger.model.ImgClassifier
import com.appham.pixbadger.util.getLabels

@Entity(tableName = "imgs")
data class ImgEntity(
        @PrimaryKey var path: String,
        @ColumnInfo(name = "file_size") var fileSize: Long,
        @ColumnInfo(name = "labels") var labels: String,
        @ColumnInfo(name = "recognitions") var recognitions: String,
        @ColumnInfo(name = "classifier_version") var classifierVersion: Int,
        @ColumnInfo(name = "resize_time") var resizeTime: Long,
        @ColumnInfo(name = "classify_time") var classifyTime: Long,
        @ColumnInfo(name = "time_stamp") var timeStamp: Long
) {
    companion object {
        fun from(img: ImgClassifier.Img) = ImgEntity(img.file.absolutePath,
                img.file.length(),
                img.recognition.getLabels(),
                img.recognition.toString(),
                ImgClassifier.VERSION,
                img.times.imgResizeTime,
                img.times.imgClassifyTime,
                System.currentTimeMillis())
    }
}
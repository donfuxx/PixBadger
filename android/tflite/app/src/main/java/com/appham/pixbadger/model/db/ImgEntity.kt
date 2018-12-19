package com.appham.pixbadger.model.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.appham.pixbadger.model.ImgClassifier

@Entity(tableName = "imgs")
data class ImgEntity(
        @PrimaryKey var path: String,
        @ColumnInfo(name = "labels") var labels: String
) {
    companion object {
        fun from(img: ImgClassifier.Img) = ImgEntity(img.file.absolutePath, img.recognition.toString())
    }
}
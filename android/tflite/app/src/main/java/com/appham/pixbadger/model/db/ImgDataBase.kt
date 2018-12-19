package com.appham.pixbadger.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [ImgEntity::class], version = 1)
abstract class ImgDataBase : RoomDatabase() {
    abstract fun imgDao(): ImgDao
}
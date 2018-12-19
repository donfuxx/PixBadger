package com.appham.pixbadger.model.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [ImgEntity::class], version = 2)
abstract class ImgDataBase : RoomDatabase() {
    abstract fun imgDao(): ImgDao
}
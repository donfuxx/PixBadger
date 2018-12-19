package com.appham.pixbadger.model.db

import android.arch.persistence.room.*

@Dao
interface ImgDao {
    @Query("SELECT * FROM imgs")
    fun getAll(): List<ImgEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg img: ImgEntity)

    @Delete
    fun delete(img: ImgEntity)
}
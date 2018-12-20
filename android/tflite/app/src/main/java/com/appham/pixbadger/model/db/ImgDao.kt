package com.appham.pixbadger.model.db

import android.arch.persistence.room.*

@Dao
interface ImgDao {
    @Query("SELECT * FROM imgs")
    fun getAll(): List<ImgEntity>

    @Query("SELECT * FROM imgs WHERE path == :filePath LIMIT 1")
    fun getImg(filePath: String): ImgEntity?

    @Query("SELECT * FROM imgs WHERE labels LIKE :label")
    fun getImgs(label: String): List<ImgEntity>?

    @Query("SELECT COUNT() FROM imgs WHERE labels LIKE :label")
    fun getLabelFacet(label: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg img: ImgEntity)

    @Delete
    fun delete(img: ImgEntity)
}
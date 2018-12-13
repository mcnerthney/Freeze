
package com.softllc.freeze.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE


@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE id = :photoId ")
    fun getPhoto(photoId: String): LiveData<Photo>

    @Query("SELECT * FROM photos ORDER BY position")
    fun getAllPhotos(): LiveData<List<Photo>>

    @Insert(onConflict = REPLACE)
    fun insert(photos: Photo)

    @Delete
    fun delete(photo: Photo)

}

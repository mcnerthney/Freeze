
package com.softllc.freeze.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

/**
 * The Data Access Object for the Plant class.
 */
@Dao
interface PhotoDao {
   //@Query("SELECT * FROM photos WHERE growZoneNumber = :growZoneNumber ORDER BY name")
    //fun getPlantsWithGrowZoneNumber(growZoneNumber: Int): LiveData<List<Plant>>

    @Query("SELECT * FROM photos WHERE id = :photoId")
    fun getPhoto(photoId: String): LiveData<Photo>

    @Insert(onConflict = REPLACE)
    fun insertAll(photos: List<Photo>)

    @Insert(onConflict = REPLACE)
    fun insert(photos: Photo)

}

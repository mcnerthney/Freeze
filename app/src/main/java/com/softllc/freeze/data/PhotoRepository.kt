package com.softllc.freeze.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.softllc.freeze.utilities.runOnIoThread
import java.sql.Time
import java.util.*

/**
 * Repository module for handling data operations.
 */
class PhotoRepository private constructor(private val photoDao: PhotoDao) {

    //fun getPhotos() = photoDao.getPhotos()
    val TAG = "djm"

    fun getPhoto(photoId: String): LiveData<Photo> {
        Log.d(TAG, "get photo $photoId")
        return photoDao.getPhoto(photoId)
    }

    fun getAllPhotos(): LiveData<List<Photo>> {
        Log.d(TAG, "get all photos")
        return photoDao.getAllPhotos()
    }


    fun insert(photo: Photo) {
        photoDao.insert(photo)
        Log.d(TAG, "updated $photo")
    }

    fun delete(photo: Photo) {
        photoDao.delete(photo)
        Log.d(TAG, "delete $photo")
    }

    //fun getPlantsWithGrowZoneNumber(growZoneNumber: Int) =
    //    photoDao.getPlantsWithGrowZoneNumber(growZoneNumber)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: PhotoRepository? = null

        fun getInstance(photoDao: PhotoDao) =
            instance ?: synchronized(this) {
                instance ?: PhotoRepository(photoDao).also { instance = it }
            }
    }

}

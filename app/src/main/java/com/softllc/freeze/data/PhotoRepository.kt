package com.softllc.freeze.data

import androidx.lifecycle.LiveData

class PhotoRepository private constructor(private val photoDao: PhotoDao) {

    fun getPhoto(photoId: String): LiveData<Photo> {
        return photoDao.getPhoto(photoId)
    }

    fun getAllPhotos(): LiveData<List<Photo>> {
        return photoDao.getAllPhotos()
    }


    fun insert(photo: Photo) {
        photoDao.insert(photo)
    }

    fun delete(photo: Photo) {
        photoDao.delete(photo)
    }

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

package com.softllc.freeze

import androidx.lifecycle.*
import com.softllc.freeze.data.Photo

import com.softllc.freeze.data.PhotoRepository
import com.softllc.freeze.utilities.runOnIoThread
import java.io.File
import java.util.*

class PhotoViewModel(
   ) : ViewModel() {


    private var photoRepository: PhotoRepository? = null
    private var photoId: String? = null

    private val _photoList = MediatorLiveData<List<Photo>>()
    private val _photo = MediatorLiveData<Photo>()

    val photos : LiveData<List<Photo>> = _photoList
    val photo : LiveData<Photo> = _photo

    init {
     //   _photoList.addSource(photoRepository.getAllPhotos(), _photoList::setValue)
     //   _photo.addSource(photoRepository.getPhoto(photoId?:""), _photo::setValue)
    }

    fun update( photo: Photo ) {
        runOnIoThread {
            photoRepository?.insert(photo)
            _photo.postValue(photo)
        }
    }

    fun rotate ( dir : Int ) {
        val photo = photo.value
        if ( photo != null ) {
            update(photo.copy(rotate = dir))
        }
    }

    fun delete( photo: Photo ) {
        runOnIoThread {
            File(photo.imageUrl).delete()
            photoRepository?.delete(photo)
        }
        _photo.postValue(null)

    }

    fun addPhoto(photoId: String, imageSrc: String) : Photo {
        val photo = Photo(photoId, imageSrc, position = Date().time, rotate = -1)
        photoRepository?.insert(photo)
        return photo
    }

    fun deleteAll() {
        val photos = _photoList.value
        if (photos != null) {
            runOnIoThread {
                for (photo in photos) {
                    File(photo.imageUrl).delete()
                    photoRepository?.delete(photo)
                }
            }
        }

    }

}

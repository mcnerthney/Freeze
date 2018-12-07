package com.softllc.freeze

import androidx.lifecycle.*
import com.softllc.freeze.data.Photo

import com.softllc.freeze.data.PhotoRepository
import com.softllc.freeze.utilities.runOnIoThread

class PhotoViewModel(
    private val photoRepository: PhotoRepository,
    private val photoId: String
) : ViewModel() {

    private val _photo = MediatorLiveData<Photo>()
    init {
        _photo.addSource( photoRepository.getPhoto(photoId), _photo::setValue)
    }

    val photo : LiveData<Photo> = _photo

    fun setImageURI( imagePath: String ) {
        val changedPhoto = Photo(photoId,imagePath)
        photoRepository.insert(changedPhoto)
        _photo.postValue(changedPhoto)
    }


    fun update( photo: Photo ) {
        runOnIoThread {
            photoRepository.insert(photo)
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
            photoRepository.delete(photo)
        }
        _photo.postValue(null)

    }


}

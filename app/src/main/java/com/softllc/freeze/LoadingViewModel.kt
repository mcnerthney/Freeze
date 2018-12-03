package com.softllc.freeze

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.softllc.freeze.data.Photo

import com.softllc.freeze.data.PhotoRepository
import com.softllc.freeze.utilities.runOnIoThread
import java.io.File

class LoadingViewModel(
    private val photoRepository: PhotoRepository,
    private val photoId: String
) : ViewModel() {

    //val isLoading: LiveData<Boolean>
    //protected val _isLoading = MutableLiveData<Boolean>()
    //val isLoading : LiveData<Boolean> = _isLoading

    private val _photo : MutableLiveData<Photo> = MutableLiveData<Photo>()
    private val dbPhoto : LiveData<Photo>

    init {
        dbPhoto = photoRepository.getPhoto(photoId)
       dbPhoto.observeForever { photo ->
            if ( photo != null ) _photo.postValue(photo)
       }
    }

    val photo : LiveData<Photo> = _photo;

    fun setImageURI( imagePath: String ) {
        val changedPhoto = Photo(photoId,imagePath)
        photoRepository.insert(changedPhoto)
        _photo.postValue(changedPhoto)
    }


    fun insert( photo: Photo ) {
        runOnIoThread {
            photoRepository.insert(photo)
            _photo.postValue(photo)
        }
    }



}

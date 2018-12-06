package com.softllc.freeze

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.softllc.freeze.data.Photo

import com.softllc.freeze.data.PhotoRepository
import com.softllc.freeze.utilities.runOnIoThread
import java.io.File
import java.util.*

class PhotoListViewModel(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _photoList = MediatorLiveData<List<Photo>>()
    init {
        _photoList.addSource(photoRepository.getAllPhotos(), _photoList::setValue)
    }

    val photos : LiveData<List<Photo>> = _photoList

    fun addPhoto(photoId: String, imageSrc: String) {
        photoRepository.insert(Photo(photoId, imageSrc, position = Date().time))
    }

}

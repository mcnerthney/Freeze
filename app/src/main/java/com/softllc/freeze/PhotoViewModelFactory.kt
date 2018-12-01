
package com.softllc.freeze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.softllc.freeze.data.PhotoRepository

class PhotoViewModelFactory(
    private val repo: PhotoRepository,
    private val photoId: String
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhotoViewModel(repo, photoId) as T
    }
}

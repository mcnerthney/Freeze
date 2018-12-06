
package com.softllc.freeze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.softllc.freeze.data.PhotoRepository

class PhotoListViewModelFactory(
    private val repo: PhotoRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PhotoListViewModel(repo) as T
    }
}

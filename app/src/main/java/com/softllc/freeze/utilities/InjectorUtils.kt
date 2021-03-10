package com.softllc.freeze.utilities

import android.content.Context
import com.softllc.freeze.data.AppDatabase
import com.softllc.freeze.data.PhotoRepository

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    fun getPhotoRepository(context: Context): PhotoRepository {
        return PhotoRepository.getInstance(AppDatabase.getInstance(context).photoDao())
    }



}

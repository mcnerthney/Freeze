/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softllc.freeze.utilities

import android.content.Context
import com.softllc.freeze.PhotoListViewModelFactory
import com.softllc.freeze.PhotoViewModelFactory
import com.softllc.freeze.data.AppDatabase
import com.softllc.freeze.data.PhotoRepository

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    fun getPhotoRepository(context: Context): PhotoRepository {
        return PhotoRepository.getInstance(AppDatabase.getInstance(context).photoDao())
    }


    fun providePhotoViewModelFactory(
        context: Context,
        plantId: String
    ): PhotoViewModelFactory {
        val repository = getPhotoRepository(context)
        return PhotoViewModelFactory(repository, plantId)
    }

    fun providePhotoListViewModelFactory(
        context: Context
    ): PhotoListViewModelFactory {
        val repository = getPhotoRepository(context)
        return PhotoListViewModelFactory(repository)
    }
    /*

    fun providePlantListViewModelFactory(context: Context): PlantListViewModelFactory {
        val repository = getPlantRepository(context)
        return PlantListViewModelFactory(repository)
    }

    fun providePlantDetailViewModelFactory(
        context: Context,
        plantId: String
    ): PlantDetailViewModelFactory {
        return PlantDetailViewModelFactory(
            getPlantRepository(context),
                getGardenPlantingRepository(context), plantId)
    }

    */
}

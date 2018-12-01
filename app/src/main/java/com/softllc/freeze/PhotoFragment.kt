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

package com.softllc.freeze

import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.content.res.ResourcesCompat.getDrawableForDensity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ORIENTATION_90
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ORIENTATION_USE_EXIF
import com.softllc.freeze.data.Photo
import com.softllc.freeze.databinding.FragmentFreezeBinding
import com.softllc.freeze.utilities.InjectorUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_freeze.*
import java.io.File
import java.io.FileInputStream

class PhotoFragment : Fragment() {

    var photoViewModel: PhotoViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("djm", "photofragment oncreateview")
        val activity = requireActivity()
        val factory = InjectorUtils.providePhotoViewModelFactory(activity, "new")
        photoViewModel = ViewModelProviders.of(activity, factory)
            .get(PhotoViewModel::class.java)



        val binding = FragmentFreezeBinding.inflate(inflater, container, false)
        binding.hasPlantings = false

        binding.imageView.orientation = ORIENTATION_90
        binding.imageView.maxScale = 40f
        photoViewModel?.photo?.observe(this, Observer { photo ->

            binding.imageView.setImage(ImageSource.uri(photo.imageUrl))
            binding.imageView.setScaleAndCenter(photo.zoom, PointF(photo.scrollX, photo.scrollY))

            //binding.imageView.setScrollPosition(photo.scrollX, photo.scrollY)
            //binding.imageView.setZoom(photo.zoom)
            Log.d("djm", "photo observed ${photo}")
        })


        // val fis = FileInputStream(File(localUri.path))
        //binding.imageView.setImageURI(Uri.fromFile(File("/data/user/0/com.softllc.freeze/app_imageDir/profile.jpg")))
        //binding.imageView.maxZoom = 10.0f

        return binding.root
    }


    override fun onPause() {
        super.onPause()
        val photo = photoViewModel?.photo?.value
        if (photo != null && image_view.scale != 0f) {
           val changePhoto = Photo(
                photo.photoId,
                photo.imageUrl,
                image_view.scale,
                image_view.center?.x ?: 0f,
                image_view.center?.y ?: 0f
            )


            photoViewModel?.insert(changePhoto)
            Log.d("djm", "photo insert ${changePhoto}")
        }
    }
}



//
//    private fun subscribeUi(adapter: GardenPlantingAdapter, binding: FragmentGardenBinding) {
//        val factory = InjectorUtils.provideGardenPlantingListViewModelFactory(requireContext())
//        val viewModel = ViewModelProviders.of(this, factory)
//                .get(GardenPlantingListViewModel::class.java)
//
//        viewModel.gardenPlantings.observe(viewLifecycleOwner, Observer { plantings ->
//            binding.hasPlantings = (plantings != null && plantings.isNotEmpty())
//        })
//
//        viewModel.plantAndGardenPlantings.observe(viewLifecycleOwner, Observer { result ->
//            if (result != null && result.isNotEmpty())
//                adapter.submitList(result)
//        })



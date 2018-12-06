
package com.softllc.freeze

import android.content.Context
import android.content.ContextWrapper
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.content.res.ResourcesCompat.getDrawableForDensity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ORIENTATION_90
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ORIENTATION_USE_EXIF
import com.google.android.material.snackbar.Snackbar
import com.softllc.freeze.data.Photo
import com.softllc.freeze.databinding.FragmentFreezeBinding
import com.softllc.freeze.utilities.InjectorUtils
import com.softllc.freeze.utilities.runOnIoThread
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_freeze.*
import java.io.*
import java.util.*

class FreezeFragment : Fragment() {

    var photoListViewModel: PhotoListViewModel? = null
    val REQUEST_GET_SINGLE_IMAGE = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val activity = requireActivity()
        val factory = InjectorUtils.providePhotoListViewModelFactory(activity)
        photoListViewModel = ViewModelProviders.of(this, factory)
            .get(PhotoListViewModel::class.java)


       val binding = FragmentFreezeBinding.inflate(inflater, container, false)
        val adapter = PhotoAdapter()
        binding.freezeList.adapter = adapter

        photoListViewModel?.photos?.observe(viewLifecycleOwner, Observer { photos ->
            if (photos != null && photos.isNotEmpty()) {
                binding.hasPhotos = true
                adapter.submitList(photos)
            } else {
                binding.hasPhotos = false
            }
        })

        binding.fab.setOnClickListener { view ->
            launchGallery()
        }


        binding.freezeList.layoutManager = GridLayoutManager(activity, 2)

//
//        binding.imageView.orientation = ORIENTATION_USE_EXIF
//        binding.imageView.maxScale = 40f
//        photoViewModel?.photo?.observe(this, Observer { photo ->
//
//            binding.imageView.setImage(ImageSource.uri(photo.imageUrl))
//            binding.imageView.setScaleAndCenter(photo.zoom, PointF(photo.scrollX, photo.scrollY))
//
//            //binding.imageView.setScrollPosition(photo.scrollX, photo.scrollY)
//            //binding.imageView.setZoom(photo.zoom)
//            Log.d("djm", "photo observed ${photo}")
//        })
//
//
//        // val fis = FileInputStream(File(localUri.path))
//        //binding.imageView.setImageURI(Uri.fromFile(File("/data/user/0/com.softllc.freeze/app_imageDir/profile.jpg")))
//        //binding.imageView.maxZoom = 10.0f

        return binding.root
    }


    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        //intent.putExtra("crop", true);
        //intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_IMAGE)
    }


    private fun cachePicture (data: Intent) {
        runOnIoThread {
            val selectedImage = data.data


            val photoId = UUID.randomUUID().toString()
            // method 1
            try {
                val inputStream = activity!!.contentResolver.openInputStream(selectedImage)
                val cw = ContextWrapper(activity)
                val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
                // Create imageDir
                val mypath = File(directory, photoId)

                val fos = FileOutputStream(mypath)
                copy(inputStream, fos)
                fos.close()

                photoListViewModel?.addPhoto(photoId, mypath.absolutePath)

                // navagate to photo fragment

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    fun copy(instream: InputStream, out: OutputStream) {
        try {

            try {
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len = instream.read(buf)
                while (len > 0) {
                    out.write(buf, 0, len)
                    len = instream.read(buf)
                }
            } finally {
                out.close()
            }
        } finally {
            instream.close()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_GET_SINGLE_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if ( data != null) {
                    cachePicture(data)
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
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



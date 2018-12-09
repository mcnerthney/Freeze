package com.softllc.freeze

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout.HORIZONTAL
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.softllc.freeze.databinding.FragmentFreezeBinding
import com.softllc.freeze.utilities.InjectorUtils
import com.softllc.freeze.utilities.ImageFile
import com.softllc.freeze.utilities.runOnIoThread
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
            if (photos != null) {
                binding.hasPhotos = photos.isNotEmpty()
                adapter.submitList(photos)
            } else {
                binding.hasPhotos = false
            }
        })

        binding.fab.setOnClickListener { view ->
            launchGallery()
        }

        FreezeApp.locked.observe(this, Observer { locked ->
            binding.keyguard = locked
        })


        binding.freezeList.layoutManager = GridLayoutManager(activity, 2)

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {


        if (requestCode == REQUEST_GET_SINGLE_IMAGE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (intent != null) {
                    val photoId = UUID.randomUUID().toString()
                    runOnIoThread {
                        val fileName = ImageFile(requireContext()).upload(photoId, intent.data?.toString() ?: "")
                        photoListViewModel?.addPhoto(photoId, fileName)
                    }
                    // navigate to photo fragment
                    val direction = FreezeFragmentDirections.actionFreezeFragmentToPhotoFragment(photoId)
                    findNavController().navigate(direction)

                }
            }
            super.onActivityResult(requestCode, resultCode, intent)
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



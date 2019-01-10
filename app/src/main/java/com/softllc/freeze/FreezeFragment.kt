package com.softllc.freeze

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.softllc.freeze.analytic.Analytic
import com.softllc.freeze.analytic.Analytic.LogAnalyticEvent
import com.softllc.freeze.databinding.FragmentFreezeBinding
import com.softllc.freeze.utilities.InjectorUtils
import com.softllc.freeze.utilities.FileUtils
import com.softllc.freeze.utilities.runOnIoThread
import java.util.*

class FreezeFragment : Fragment() {

    lateinit var photoListViewModel: PhotoViewModel
    val REQUEST_GET_SINGLE_IMAGE = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val activity = requireActivity()
        val factory = InjectorUtils.providePhotoViewModelFactory(activity, "")
        photoListViewModel = ViewModelProviders.of(this, factory)
            .get(PhotoViewModel::class.java)


        val binding = FragmentFreezeBinding.inflate(inflater, container, false)
        val adapter = PhotoAdapter()
        binding.freezeList.adapter = adapter

        photoListViewModel.photos.observe(viewLifecycleOwner, Observer { photos ->
            if (photos != null) {
                binding.hasPhotos = photos.isNotEmpty()
                adapter.submitList(photos)
            } else {
                binding.hasPhotos = false
            }
        })

        FreezeApp.locked.observe(this, Observer { locked ->
            binding.keyguard = locked
        })


        binding.freezeList.layoutManager = GridLayoutManager(activity, 2)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_freeze, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete -> {
                photoListViewModel.deleteAll()
                true

            }
            R.id.action_add -> {
                launchGallery()
                true

            }
             else -> super.onOptionsItemSelected(item)
        }
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
                        val photo = photoListViewModel.addPhoto(photoId, "")
                        val filename = FileUtils(requireContext()).upload(photoId, intent.data?.toString() ?: "")
                        photoListViewModel.update(photo.copy(imageUrl = filename))
                        context?.LogAnalyticEvent(Analytic.Event.ADD_PHOTO, "get_single")
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
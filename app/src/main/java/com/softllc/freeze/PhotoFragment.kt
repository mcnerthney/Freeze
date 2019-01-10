
package com.softllc.freeze


import android.graphics.PointF
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.davemorrissey.labs.subscaleview.ImageSource
import com.softllc.freeze.analytic.Analytic.LogAnalyticEvent
import com.softllc.freeze.analytic.Analytic
import com.softllc.freeze.data.Photo
import com.softllc.freeze.databinding.FragmentPhotoBinding
import com.softllc.freeze.utilities.InjectorUtils

class PhotoFragment : Fragment() {

    var photoViewModel: PhotoViewModel? = null
    lateinit var binding: FragmentPhotoBinding
    lateinit var photoId: String
    var currentPhoto: Photo? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        photoId = PhotoFragmentArgs.fromBundle(arguments).photoId
        val activity = requireActivity()
        val factory = InjectorUtils.providePhotoViewModelFactory(activity, photoId)
        photoViewModel = ViewModelProviders.of(this, factory)
            .get(PhotoViewModel::class.java)


        binding = FragmentPhotoBinding.inflate(inflater, container, false)

        binding.imageView.maxScale = 40f
        photoViewModel?.photo?.observe(this, Observer { photo ->

            if (photo != null) {
                if (currentPhoto == null || currentPhoto != photo) {

                    binding.imageView.orientation = photo.rotate
                    binding.imageView.setImage(ImageSource.uri(photo.imageUrl))
                    binding.imageView.setScaleAndCenter(photo.zoom, PointF(photo.scrollX, photo.scrollY))

                    currentPhoto = photo
                }
            }

        })
        photoViewModel?.photos?.observe(this, Observer { photo ->
            setMenuItems()
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_photo, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    var menuItemNext : MenuItem? = null
    var menuItemPrev : MenuItem? = null

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menuItemNext = menu?.findItem(R.id.action_next)
        menuItemPrev = menu?.findItem(R.id.action_prev)
        setMenuItems()
    }

    private fun setMenuItems () {
        menuItemNext?.setEnabled(nextPhotoId()!=null)
        menuItemPrev?.setEnabled(prevPhotoId()!=null)

    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete -> {
                context?.LogAnalyticEvent(Analytic.Event.DELETE_PHOTO)

                var next : String? = null
                next = nextPhotoId()
                if ( next == null ) next = prevPhotoId()

                if (photoViewModel?.photo?.value != null) {

                    photoViewModel?.delete(photoViewModel?.photo?.value ?: Photo("", ""))

                    if ( next == null ) {
                        findNavController().popBackStack()
                    }
                    else {
                        val direction = PhotoFragmentDirections.ActionPhotoFragmentNext(next)
                        findNavController().navigate(direction)
                    }
                }
                true

            }
            R.id.action_rotate -> {
                context?.LogAnalyticEvent(Analytic.Event.ROTATE_PHOTO)
                val photo = photoViewModel?.photo?.value
                if (photo != null) {
                    photoViewModel?.rotate(
                        when (photo.rotate) {
                            -1 -> 0
                            0 -> 90
                            90 -> 180
                            180 -> 270
                            else -> -1
                        }
                    )
                }

                true
            }
            R.id.action_next -> {
                context?.LogAnalyticEvent(Analytic.Event.NEXT_PHOTO)
                val id = nextPhotoId()
                if (id != null) {
                    // navigate to photo fragment
                    val direction = PhotoFragmentDirections.ActionPhotoFragmentNext(id)
                    findNavController().navigate(direction)

                }

                true
            }
            R.id.action_prev -> {
                context?.LogAnalyticEvent(Analytic.Event.PREV_PHOTO)
                val id = prevPhotoId()
                if (id != null) {
                    // navigate to photo fragment
                    val direction = PhotoFragmentDirections.ActionPhotoFragmentPrev(id)
                    findNavController().navigate(direction)

                }
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun nextPhotoId(): String? {
        val photos = photoViewModel?.photos?.value
        if ( photos != null ) {
            var matched  = false
            for ( p in photos ) {
                if ( p.photoId == photoId) {
                    matched = true
                    continue
                }
                if ( matched ) {
                    return p.photoId
                }
            }
        }
        return null
    }

    private fun prevPhotoId(): String? {
        val photos = photoViewModel?.photos?.value
        if ( photos != null ) {
            var last : Photo? = null
            for ( p in photos ) {
                if ( p.photoId == photoId) {
                    return last?.photoId
                }
                last = p
            }
        }
        return null
    }


    override fun onPause() {
        super.onPause()
        val photo = photoViewModel?.photo?.value
        if (photo != null && binding.imageView.scale != 0f) {
            val changePhoto = photo.copy(
                zoom = binding.imageView.scale,
                scrollX = binding.imageView.center?.x ?: 0f,
                scrollY = binding.imageView.center?.y ?: 0f
            )

            if ( !changePhoto.equals(photo)) {
                context?.LogAnalyticEvent(Analytic.Event.SCALE_PHOTO)
                photoViewModel?.update(changePhoto)
            }
        }
    }

}




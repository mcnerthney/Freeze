
package com.softllc.freeze

import android.graphics.PointF
import androidx.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.fitCenterTransform
import com.davemorrissey.labs.subscaleview.ImageSource
import com.softllc.freeze.data.Photo

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}



@BindingAdapter("isShown")
fun bindIsShown(view: View, show: Boolean) {
    view.visibility = if (!show) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("scaleImage")
fun bindImageFromUrl(view: ImageView, photo: Photo?) {
    if (photo != null) {
        Glide.with(view.context)
            .load(photo.imageUrl)
            .apply(fitCenterTransform())
            .into(view)
    }
}

@BindingAdapter("scaleSubImage")
fun bindImageFromUrl(view: com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView, photo: Photo?) {
    if (photo != null) {
        view.orientation = photo.rotate
        view.setImage(ImageSource.uri(photo.imageUrl))
        view.setScaleAndCenter(photo.zoom, PointF(photo.scrollX, photo.scrollY))

    }
}



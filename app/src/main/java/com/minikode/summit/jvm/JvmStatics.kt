package com.minikode.summit.jvm

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.minikode.summit.R

class JvmStatics {
    companion object {
        @JvmStatic
        @BindingAdapter("app:imageUrl")
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            if (imageUrl?.isNotEmpty() == true) {
                Glide.with(imageView.context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_404)
                    .into(imageView)
            } else {
                Glide.with(imageView.context)
                    .load(R.drawable.image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_404)
                    .into(imageView)
            }

        }
    }
}
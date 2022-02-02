package com.ebenezer.gana.shoppy.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ebenezer.gana.shoppy.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the imageView
            Glide.with(context)
                .load(image) // URI of the image
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_user_placeholder) // Default placeholder if the image fails to load
                .into(imageView) // the view the image will be loaded into

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun loadProductPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the imageView
            Glide.with(context)
                .load(image) // URI of the image
                .centerCrop() // Scale type of the image.
                .into(imageView) // the view the image will be loaded into

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
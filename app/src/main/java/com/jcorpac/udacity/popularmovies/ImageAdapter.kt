package com.jcorpac.udacity.popularmovies

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.util.*

internal class ImageAdapter(context: Activity, private val posterURLArray: ArrayList<String?>) : ArrayAdapter<String>(context, 0, posterURLArray) {

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val imageView: ImageView

        if (convertView == null) {
            imageView = ImageView(context)
        } else {
            imageView = convertView as ImageView
        }

        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.minimumHeight = 500

        Picasso.with(context)
                .load(posterURLArray[position])
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView)

        return imageView
    }
}
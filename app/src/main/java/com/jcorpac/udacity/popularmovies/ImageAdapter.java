package com.jcorpac.udacity.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class ImageAdapter extends ArrayAdapter<String> {

    private ArrayList<String> posterURLArray;

    ImageAdapter(Activity context, ArrayList<String> urlList){
        super(context, 0, urlList);
        posterURLArray = urlList;
    }

    // create a new ImageView for each item referenced by the Adapter
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(getContext());
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setMinimumHeight(500);

        Picasso.with(getContext())
                .load(posterURLArray.get(position))
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView);

        return imageView;
    }
}
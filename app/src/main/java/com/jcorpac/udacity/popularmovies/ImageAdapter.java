package com.jcorpac.udacity.popularmovies;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<String> {

    ArrayList<String> posterURLArray;

    public ImageAdapter(Activity context, ArrayList<String> urlList){
        super(context, 0, urlList);
        posterURLArray = urlList;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(getContext());
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setMinimumHeight(500);

        Picasso.with(getContext()).load(posterURLArray.get(position)).into(imageView);

        return imageView;
    }
}
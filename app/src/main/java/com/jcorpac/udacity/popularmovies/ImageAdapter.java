package com.jcorpac.udacity.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] posterURLArray;

    public ImageAdapter(Context c, String[] posterURLArray) {
        mContext = c;
        this.posterURLArray = posterURLArray;
    }

    public int getCount() {
        return posterURLArray.length;
    }

    public Object getItem(int position) {
        return posterURLArray[position];
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(posterURLArray[position]).into(imageView);
        return imageView;
    }
}
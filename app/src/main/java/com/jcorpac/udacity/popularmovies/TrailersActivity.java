package com.jcorpac.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class TrailersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_trailers);
    }

    public static final String TRAILERS_URI_TAG = "trailersUri";
    public static final String TRAILERS_MOVIE_TAG = "movieTitle";

    public static Intent newIntent(Context context, Uri reviewsUri, String movieTitle){
        Intent intent = new Intent(context, TrailersActivity.class);
        intent.putExtra(TRAILERS_URI_TAG, reviewsUri);
        intent.putExtra(TRAILERS_MOVIE_TAG, movieTitle);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

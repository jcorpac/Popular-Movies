package com.jcorpac.udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcorpac.udacity.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity {

    private String LOG_TAG = this.getClass().getSimpleName();
    private Movie thisMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    protected void onStart() {
        super.onStart();

        extractIntent();
        displayMovieDetails();

    }

    private void extractIntent() {
        Intent incomingMovie = getIntent();
        if (incomingMovie == null) { finish(); }
        else {
            if (incomingMovie.hasExtra(Constants.DETAIL_INTENT_LABEL)) {
                String jsonMovieString = incomingMovie.getStringExtra(Constants.DETAIL_INTENT_LABEL);
                try {
                    thisMovie = new Movie(new JSONObject(jsonMovieString));
                } catch (JSONException jse) {
                    Log.e(LOG_TAG, "Error parsing JSON string for detail View");
                    jse.printStackTrace();
                    finish();
                }
            } else {
                finish();
            }
        }
    }

    private void displayMovieDetails() {
        this.setTitle(thisMovie.getTitle());

        TextView movieTitle = (TextView)findViewById(R.id.txtMovieTitle);
        movieTitle.setText(thisMovie.getTitle());

        TextView movieSummary = (TextView)findViewById(R.id.txtSummaryText);
        movieSummary.setText(thisMovie.getSummary());

        ImageView moviePoster = (ImageView)findViewById(R.id.imgPoster);
        String posterURL = Constants.POSTER_BASE_URL +Constants.POSTER_RES+thisMovie.getPosterURL();
        Picasso.with(this)
                .load(posterURL)
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_placeholder)
                .into(moviePoster);

        TextView userRating = (TextView)findViewById(R.id.txtUserRating);
        userRating.setText(String.valueOf(thisMovie.getVoteAverage()));

        TextView releaseDate = (TextView)findViewById(R.id.txtReleaseDate);
        releaseDate.setText(thisMovie.getReleaseDate());
    }

    public void loadPreviews(View view) {
        Intent previewsIntent = new Intent(Intent.ACTION_VIEW, thisMovie.getTrailersUri());
        if(previewsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(previewsIntent);
        }

    }
}

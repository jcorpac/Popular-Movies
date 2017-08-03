package com.jcorpac.udacity.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry;
import com.jcorpac.udacity.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private String LOG_TAG = this.getClass().getSimpleName();
    private Movie thisMovie;

    private ImageView imgFavoriteStar;
    private TextView txtFavoriteLabel;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        txtFavoriteLabel = (TextView) findViewById(R.id.txtFavLabel);
        imgFavoriteStar = (ImageView) findViewById(R.id.imgFavStar);
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
                thisMovie = incomingMovie.getParcelableExtra(Constants.DETAIL_INTENT_LABEL);
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

        Cursor favoriteEntry = getContentResolver().query(FavoritesEntry.CONTENT_URI.buildUpon().appendPath(thisMovie.getMovieID()).build(),
                null, null, null, FavoritesEntry._ID);
        isFavorite = (favoriteEntry != null && favoriteEntry.getCount() > 0);

        View favoritesIcon = findViewById(R.id.viewFavoriteIcon);
        favoritesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        displayFavorite();
    }

    private void displayFavorite() {
        if(isFavorite){
            imgFavoriteStar.setColorFilter(ContextCompat.getColor(this, R.color.colorFavoriteEnabled));
            txtFavoriteLabel.setText(getString(R.string.strFavorite));
        } else {
            imgFavoriteStar.setColorFilter(ContextCompat.getColor(this, R.color.colorFavoriteDisabled));
            txtFavoriteLabel.setText(getString(R.string.strNotFavorite));
        }
    }

    private void toggleFavorite() {
        if(isFavorite) {
            removeFavorite();
        } else {
            setFavorite();
        }
        displayFavorite();
    }

    private void setFavorite() {
        ContentValues movieFavorite = new ContentValues();
        movieFavorite.put(FavoritesEntry.COLUMN_MOVIE_ID, thisMovie.getMovieID());
        movieFavorite.put(FavoritesEntry.COLUMN_TITLE, thisMovie.getTitle());
        movieFavorite.put(FavoritesEntry.COLUMN_SUMMARY, thisMovie.getSummary());
        movieFavorite.put(FavoritesEntry.COLUMN_POSTER_URL, thisMovie.getPosterURL());
        movieFavorite.put(FavoritesEntry.COLUMN_RELEASE_DATE, thisMovie.getReleaseDate());
        movieFavorite.put(FavoritesEntry.COLUMN_VOTE_AVERAGE, thisMovie.getVoteAverage());

        Uri responseUri = getContentResolver().insert(FavoritesEntry.CONTENT_URI, movieFavorite);
        if(responseUri != null) {
            isFavorite = true;
        }
    }

    private void removeFavorite() {
        int numFavoritesDeleted = getContentResolver().delete(FavoritesEntry.CONTENT_URI.buildUpon().appendPath(thisMovie.getMovieID()).build(), null, null);
        if(numFavoritesDeleted > 0){
            isFavorite = false;
        }
    }

    public void playYoutubeVideo(String videoId) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

}

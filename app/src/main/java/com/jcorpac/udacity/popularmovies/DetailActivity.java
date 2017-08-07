package com.jcorpac.udacity.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry;
import com.jcorpac.udacity.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Movie thisMovie;

    private ImageView imgFavoriteStar;
    private TextView txtFavoriteLabel;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtFavoriteLabel = (TextView) findViewById(R.id.img_favorite_label);
        imgFavoriteStar = (ImageView) findViewById(R.id.img_favorite_star);
    }

    @Override
    protected void onStart() {
        super.onStart();

        extractIntent();
        displayMovieDetails();
    }

    public static final String DETAIL_MOVIE_TAG = "MOVIE_DETAILS";
    public static Intent newIntent(Context context, Movie movie){
        Intent toDetailView = new Intent(context, DetailActivity.class);
        toDetailView.putExtra(DETAIL_MOVIE_TAG, movie);
        return toDetailView;
    }

    private void extractIntent() {
        Intent incomingMovie = getIntent();
        if (incomingMovie == null) { finish(); }
        else {
            if (incomingMovie.hasExtra(DETAIL_MOVIE_TAG)) {
                thisMovie = incomingMovie.getParcelableExtra(DETAIL_MOVIE_TAG);
            } else {
                finish();
            }
        }
    }

    private void displayMovieDetails() {
        this.setTitle(thisMovie.getTitle());

        TextView movieTitle = (TextView)findViewById(R.id.txt_movie_title);
        movieTitle.setText(thisMovie.getTitle());

        TextView movieSummary = (TextView)findViewById(R.id.txt_summary_text);
        movieSummary.setText(thisMovie.getSummary());

        ImageView moviePoster = (ImageView)findViewById(R.id.img_poster);
        String posterURL = Constants.POSTER_BASE_URL +Constants.POSTER_RES+thisMovie.getPosterURL();
        Picasso.with(this)
                .load(posterURL)
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_placeholder)
                .into(moviePoster);

        TextView userRating = (TextView)findViewById(R.id.txt_user_rating);
        userRating.setText(String.valueOf(thisMovie.getVoteAverage()));

        TextView releaseDate = (TextView)findViewById(R.id.txt_release_date);
        releaseDate.setText(thisMovie.getReleaseDate());

        Cursor favoriteEntry = getContentResolver().query(FavoritesEntry.CONTENT_URI.buildUpon().appendPath(thisMovie.getMovieID()).build(),
                    null, null, null, FavoritesEntry._ID);
        isFavorite = (favoriteEntry != null && favoriteEntry.getCount() > 0);
        if(favoriteEntry != null)
            favoriteEntry.close();

        View favoritesIcon = findViewById(R.id.view_favorite_icon);
        favoritesIcon.setOnClickListener(this);

        Button btnTrailers = (Button)findViewById(R.id.btn_view_trailers);
        btnTrailers.setOnClickListener(this);

        Button btnReviews = (Button)findViewById(R.id.btn_read_reviews);
        btnReviews.setOnClickListener(this);

        displayFavorite();
    }

    private void displayFavorite() {
        if(isFavorite){
            imgFavoriteStar.setColorFilter(ContextCompat.getColor(this, R.color.color_favorite_enabled));
            txtFavoriteLabel.setText(getString(R.string.favorite));
        } else {
            imgFavoriteStar.setColorFilter(ContextCompat.getColor(this, R.color.color_favorite_disabled));
            txtFavoriteLabel.setText(getString(R.string.not_favorite));
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
        int numFavoritesDeleted = getContentResolver().delete(FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(thisMovie.getMovieID()).build(), null, null);
        if(numFavoritesDeleted > 0){
            isFavorite = false;
            getContentResolver().notifyChange(FavoritesEntry.CONTENT_URI, null);
        }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.view_favorite_icon:
                toggleFavorite();
                break;
            case R.id.btn_view_trailers:
                startActivity(TrailersActivity.newIntent(this, thisMovie.getTrailersUri(), thisMovie.getTitle()));
                break;
            case R.id.btn_read_reviews:
                startActivity(ReviewsActivity.newIntent(this, thisMovie.getReviewsUri(), thisMovie.getTitle()));
                break;
        }
    }
}

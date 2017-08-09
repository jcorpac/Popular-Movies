package com.jcorpac.udacity.popularmovies

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry
import com.jcorpac.udacity.popularmovies.model.Movie
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    internal var thisMovie: Movie? = null

    private lateinit var imgFavoriteStar: ImageView
    private lateinit var txtFavoriteLabel: TextView

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtFavoriteLabel = findViewById(R.id.img_favorite_label) as TextView
        imgFavoriteStar = findViewById(R.id.img_favorite_star) as ImageView
    }

    override fun onStart() {
        super.onStart()

        extractIntent()
        displayMovieDetails()
    }

    private fun extractIntent() {
        val incomingMovie = intent
        if (incomingMovie == null) {
            finish()
        } else {
            if (incomingMovie.hasExtra(DETAIL_MOVIE_TAG)) {
                thisMovie = incomingMovie.getParcelableExtra<Movie>(DETAIL_MOVIE_TAG)
            } else {
                finish()
            }
        }
    }

    private fun displayMovieDetails() {
        this.title = thisMovie?.title

        val movieTitle = findViewById(R.id.txt_movie_title) as TextView
        movieTitle.text = thisMovie?.title

        val movieSummary = findViewById(R.id.txt_summary_text) as TextView
        movieSummary.text = thisMovie?.summary

        val moviePoster = findViewById(R.id.img_poster) as ImageView
        val posterURL = Constants.POSTER_BASE_URL + Constants.POSTER_RES + thisMovie?.posterURL
        Picasso.with(this)
                .load(posterURL)
                .error(R.drawable.ic_error)
                .placeholder(R.drawable.ic_placeholder)
                .into(moviePoster)

        val userRating = findViewById(R.id.txt_user_rating) as TextView
        userRating.text = thisMovie?.voteAverage.toString()

        val releaseDate = findViewById(R.id.txt_release_date) as TextView
        releaseDate.text = thisMovie?.releaseDate

        val favoriteEntry = contentResolver.query(FavoritesEntry.CONTENT_URI.buildUpon().appendPath(thisMovie?.movieID).build(), null, null, null, FavoritesEntry._ID)
        isFavorite = favoriteEntry != null && favoriteEntry.count > 0
        favoriteEntry?.close()

        val favoritesIcon = findViewById(R.id.view_favorite_icon)
        favoritesIcon.setOnClickListener(this)

        val btnTrailers = findViewById(R.id.btn_view_trailers) as Button
        btnTrailers.setOnClickListener(this)

        val btnReviews = findViewById(R.id.btn_read_reviews) as Button
        btnReviews.setOnClickListener(this)

        displayFavorite()
    }

    private fun displayFavorite() {
        if (isFavorite) {
            imgFavoriteStar.setColorFilter(ContextCompat.getColor(this, R.color.color_favorite_enabled))
            txtFavoriteLabel.text = getString(R.string.favorite)
        } else {
            imgFavoriteStar.setColorFilter(ContextCompat.getColor(this, R.color.color_favorite_disabled))
            txtFavoriteLabel.text = getString(R.string.not_favorite)
        }
    }

    private fun toggleFavorite() {
        if (isFavorite) {
            removeFavorite()
        } else {
            setFavorite()
        }
        displayFavorite()
    }

    private fun setFavorite() {
        val movieFavorite = ContentValues()
        movieFavorite.put(FavoritesEntry.COLUMN_MOVIE_ID, thisMovie?.movieID)
        movieFavorite.put(FavoritesEntry.COLUMN_TITLE, thisMovie?.title)
        movieFavorite.put(FavoritesEntry.COLUMN_SUMMARY, thisMovie?.summary)
        movieFavorite.put(FavoritesEntry.COLUMN_POSTER_URL, thisMovie?.posterURL)
        movieFavorite.put(FavoritesEntry.COLUMN_RELEASE_DATE, thisMovie?.releaseDate)
        movieFavorite.put(FavoritesEntry.COLUMN_VOTE_AVERAGE, thisMovie?.voteAverage)

        val responseUri = contentResolver.insert(FavoritesEntry.CONTENT_URI, movieFavorite)
        if (responseUri != null) {
            isFavorite = true
        }
    }

    private fun removeFavorite() {
        val numFavoritesDeleted = contentResolver.delete(FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(thisMovie?.movieID).build(), null, null)
        if (numFavoritesDeleted > 0) {
            isFavorite = false
            contentResolver.notifyChange(FavoritesEntry.CONTENT_URI, null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.view_favorite_icon -> toggleFavorite()
            R.id.btn_view_trailers -> startActivity(TrailersActivity.newIntent(this, thisMovie?.getTrailersUri()!!, thisMovie?.title!!))
            R.id.btn_read_reviews -> startActivity(ReviewsActivity.newIntent(this, thisMovie?.getReviewsUri()!!, thisMovie?.title!!))
        }
    }

    companion object {

        val DETAIL_MOVIE_TAG = "MOVIE_DETAILS"
        fun newIntent(context: Context, movie: Movie?): Intent {
            val toDetailView = Intent(context, DetailActivity::class.java)
            toDetailView.putExtra(DETAIL_MOVIE_TAG, movie)
            return toDetailView
        }
    }
}

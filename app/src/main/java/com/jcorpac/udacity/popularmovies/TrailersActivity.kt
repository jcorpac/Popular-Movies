package com.jcorpac.udacity.popularmovies

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

class TrailersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_trailers)
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

    companion object {

        val TRAILERS_URI_TAG = "trailersUri"
        val TRAILERS_MOVIE_TAG = "movieTitle"

        fun newIntent(context: Context, reviewsUri: Uri, movieTitle: String): Intent {
            val intent = Intent(context, TrailersActivity::class.java)
            intent.putExtra(TRAILERS_URI_TAG, reviewsUri)
            intent.putExtra(TRAILERS_MOVIE_TAG, movieTitle)
            return intent
        }
    }
}

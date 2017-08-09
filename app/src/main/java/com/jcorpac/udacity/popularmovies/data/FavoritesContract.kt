package com.jcorpac.udacity.popularmovies.data

import android.net.Uri
import android.provider.BaseColumns

// Database Contract for Favorites data provider
object FavoritesContract {
    internal val AUTHORITY = "com.jcorpac.udacity.popularmovies"
    private val BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY)
    internal val PATH_FAVORITES = "favorites"

    class FavoritesEntry : BaseColumns {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build()

            val TABLE_NAME = "favorites"

            val COLUMN_MOVIE_ID = "movie_id"
            val COLUMN_TITLE = "title"
            val COLUMN_SUMMARY = "summary"
            val COLUMN_POSTER_URL = "posterURL"
            val COLUMN_RELEASE_DATE = "releaseDate"
            val COLUMN_VOTE_AVERAGE = "voteAverage"
            val _ID = "_id"
            val _COUNT = "_count"
        }
    }
}

package com.jcorpac.udacity.popularmovies

object Constants {
    val API_KEY = BuildConfig.THE_MOVIES_DB_API_KEY //Add API key in gradle.properties
    val BASE_URL = "http://api.themoviedb.org/3/"
    val MOVIES_SERVICE_URL = BASE_URL + "discover/movie"
    val POPULAR_MOVIES_URL = BASE_URL + "movie/popular"
    val TOP_RATED_MOVIES_URL = BASE_URL + "movie/top_rated"
    val POSTER_BASE_URL = "http://image.tmdb.org/t/p/"
    val THUMBNAIL_RES = "w185/"
    val POSTER_RES = "original/"
}
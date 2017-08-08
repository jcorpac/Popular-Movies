package com.jcorpac.udacity.popularmovies;

public class Constants {
    public static final String API_KEY = BuildConfig.THE_MOVIES_DB_API_KEY; //Add API key in gradle.properties
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String MOVIES_SERVICE_URL = BASE_URL+"discover/movie";
    public static final String POPULAR_MOVIES_URL = BASE_URL+"movie/popular";
    public static final String TOP_RATED_MOVIES_URL = BASE_URL+"movie/top_rated";
    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String THUMBNAIL_RES = "w185/";
    public static final String POSTER_RES = "original/";
}
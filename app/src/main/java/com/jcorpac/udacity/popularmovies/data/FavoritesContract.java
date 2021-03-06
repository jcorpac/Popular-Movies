package com.jcorpac.udacity.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

// Database Contract for Favorites data provider
public class FavoritesContract {
    static final String AUTHORITY = "com.jcorpac.udacity.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    static final String PATH_FAVORITES = "favorites";

    public static final class FavoritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_POSTER_URL = "posterURL";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
    }
}

package com.jcorpac.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry;

class FavoritesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoritesDb.db";
    private static final int DATABASE_VERSION = 1;

    FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Sample update query string.
    private static final String DB_UPDATE_1_TO_2 = "ALTER TABLE " + FavoritesEntry.TABLE_NAME
            + " ADD COLUMN movieTrailerUri STRING";

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_SQL = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY, " +
                FavoritesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_SUMMARY + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                "UNIQUE (" + FavoritesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*  Sample update code. Current DB version is still 1
        if(oldVersion < 2) {
            db.execSQL(DB_UPDATE_1_TO_2);
        }
        */
    }
}

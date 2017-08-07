package com.jcorpac.udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry;

public class FavoritesProvider extends ContentProvider {

    public static final int ALL_FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, ALL_FAVORITES);
        matcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES+"/#", FAVORITE_WITH_ID);

        return matcher;
    }

    private FavoritesDbHelper mFavoritesDbHelper;

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int favoritesDeleted;

        switch(match){
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                favoritesDeleted = db.delete(FavoritesEntry.TABLE_NAME,
                        FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{id});
                break;
            case ALL_FAVORITES:
                throw new UnsupportedOperationException("Deleting all favorites not supported at this time.");
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favoritesDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return favoritesDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch(match){
            case ALL_FAVORITES: return "vnd.android.cursor.dir/" + FavoritesContract.AUTHORITY + "/" + FavoritesContract.PATH_FAVORITES;
            case FAVORITE_WITH_ID: return "vnd.android.cursor.item/" + FavoritesContract.AUTHORITY + "/" + FavoritesContract.PATH_FAVORITES;
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase writeableDb = mFavoritesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case ALL_FAVORITES:
                long id = writeableDb.insert(FavoritesEntry.TABLE_NAME, null, values);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(FavoritesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri.toString());
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoritesDbHelper = new FavoritesDbHelper(context);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase readableDb = mFavoritesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match){
            case ALL_FAVORITES:
                returnCursor = readableDb.query(FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                returnCursor = readableDb.query(FavoritesEntry.TABLE_NAME,
                        projection,
                        FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int favoritesUpdated;
        int match = sUriMatcher.match(uri);

        switch(match) {
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                favoritesUpdated = mFavoritesDbHelper.getWritableDatabase().update(FavoritesEntry.TABLE_NAME,
                        values,
                        FavoritesEntry._ID + "=?",
                        new String[]{id});
                break;
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(favoritesUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return favoritesUpdated;
    }
}

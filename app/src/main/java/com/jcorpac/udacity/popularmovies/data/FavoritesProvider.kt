package com.jcorpac.udacity.popularmovies.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.jcorpac.udacity.popularmovies.data.FavoritesContract.FavoritesEntry

class FavoritesProvider : ContentProvider() {

    private var mFavoritesDbHelper: FavoritesDbHelper? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mFavoritesDbHelper!!.writableDatabase

        val match = sUriMatcher.match(uri)
        val favoritesDeleted: Int

        when (match) {
            FAVORITE_WITH_ID -> {
                val id = uri.pathSegments[1]
                favoritesDeleted = db.delete(FavoritesEntry.TABLE_NAME,
                        FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                        arrayOf(id))
            }
            ALL_FAVORITES -> throw UnsupportedOperationException("Deleting all favorites not supported at this time.")
            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }

        if (favoritesDeleted != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return favoritesDeleted
    }

    override fun getType(uri: Uri): String? {
        val match = sUriMatcher.match(uri)

        when (match) {
            ALL_FAVORITES -> return "vnd.android.cursor.dir/" + FavoritesContract.AUTHORITY + "/" + FavoritesContract.PATH_FAVORITES
            FAVORITE_WITH_ID -> return "vnd.android.cursor.item/" + FavoritesContract.AUTHORITY + "/" + FavoritesContract.PATH_FAVORITES
            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val writeableDb = mFavoritesDbHelper!!.writableDatabase

        val match = sUriMatcher.match(uri)
        val returnUri: Uri

        when (match) {
            ALL_FAVORITES -> {
                val id = writeableDb.insert(FavoritesEntry.TABLE_NAME, null, values)
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoritesEntry.CONTENT_URI, id)
                } else {
                    throw android.database.SQLException("Failed to insert row into " + uri.toString())
                }
            }
            else -> throw UnsupportedOperationException("Unknown uri: " + uri.toString())
        }

        context!!.contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun onCreate(): Boolean {
        val context = context
        mFavoritesDbHelper = FavoritesDbHelper(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val readableDb = mFavoritesDbHelper!!.readableDatabase
        val match = sUriMatcher.match(uri)
        val returnCursor: Cursor

        when (match) {
            ALL_FAVORITES -> returnCursor = readableDb.query(FavoritesEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs, null, null,
                    sortOrder)
            FAVORITE_WITH_ID -> {
                val movieId = uri.lastPathSegment
                returnCursor = readableDb.query(FavoritesEntry.TABLE_NAME,
                        projection,
                        FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                        arrayOf(movieId), null, null,
                        sortOrder)
            }
            else -> throw UnsupportedOperationException("Unknown uri: " + uri.toString())
        }
        returnCursor.setNotificationUri(context!!.contentResolver, uri)
        return returnCursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val favoritesUpdated: Int
        val match = sUriMatcher.match(uri)

        when (match) {
            FAVORITE_WITH_ID -> {
                val id = uri.pathSegments[1]
                favoritesUpdated = mFavoritesDbHelper!!.writableDatabase.update(FavoritesEntry.TABLE_NAME,
                        values,
                        FavoritesEntry._ID + "=?",
                        arrayOf(id))
            }
            else -> throw UnsupportedOperationException("Unknown uri: " + uri)
        }
        if (favoritesUpdated != 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return favoritesUpdated
    }

    companion object {

        val ALL_FAVORITES = 100
        val FAVORITE_WITH_ID = 101
        private val sUriMatcher = buildUriMatcher()

        private fun buildUriMatcher(): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, ALL_FAVORITES)
            matcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID)

            return matcher
        }
    }
}
